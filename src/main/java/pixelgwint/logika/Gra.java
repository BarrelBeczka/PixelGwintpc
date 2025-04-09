package pixelgwint.logika;

import pixelgwint.model.Karta;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.Random;

public class Gra {
    private final List<Karta> rzadBliskiGracz1 = new ArrayList<>();
    private final List<Karta> rzadSrodkowyGracz1 = new ArrayList<>();
    private final List<Karta> rzadDalszyGracz1 = new ArrayList<>();
    private final List<Karta> rzadBliskiGracz2 = new ArrayList<>();
    private final List<Karta> rzadSrodkowyGracz2 = new ArrayList<>();
    private final List<Karta> rzadDalszyGracz2 = new ArrayList<>();
    private final List<Karta> kartySpecjalneGracz1 = new ArrayList<>();
    private final List<Karta> kartySpecjalneGracz2 = new ArrayList<>();
    private final List<Karta> cmentarzGracz1 = new ArrayList<>();
    private final List<Karta> cmentarzGracz2 = new ArrayList<>();
    private final List<Karta> kartyPogodowe = new ArrayList<>();
    private final List<Karta> rekaGracz1;
    private final List<Karta> rekaGracz2;
    private final Karta dowodcaGracz1;
    private final Karta dowodcaGracz2;
    private int aktualnyGracz;

    private int zetonyZyciaGracz1 = 2;
    private int zetonyZyciaGracz2 = 2;

    private boolean gracz1Pasowal = false;
    private boolean gracz2Pasowal = false;

    private List<Karta> taliaGracza1;
    private List<Karta> taliaGracza2;

    private final String taliaNazwaGracza1;
    private final String taliaNazwaGracza2;

    private final String umiejetnoscTaliiGracza1;
    private final String umiejetnoscTaliiGracza2;

    private boolean dowodcaAktywowanyGracz1 = false;
    private boolean dowodcaAktywowanyGracz2 = false;

    private Map<String, Integer> efektyDowodcowGracz1 = new HashMap<>();
    private Map<String, Integer> efektyDowodcowGracz2 = new HashMap<>();
    private boolean dowodcaUzytyGracz1 = false;
    private boolean dowodcaUzytyGracz2 = false;

    private boolean emhyrNajeźdźcaAktywny = false;
    private boolean emhyrBiałyPłomieńAktywny = false;


    private String sprawdzDowodce(int gracz) {
        Karta dowodca = (gracz == 1) ? dowodcaGracz1 : dowodcaGracz2;
        if (dowodca == null) return "";

        String nazwa = dowodca.getNazwa();

        // Foltesty (Królestwo Północy)
        if (nazwa.contains("Foltest")) {
            if (nazwa.contains("Zdobywca")) return "Zdobywca";
            if (nazwa.contains("Dowódca Północy")) return "Dowódca_Północy";
            if (nazwa.contains("Król Temerii")) return "Król_Temerii";
            if (nazwa.contains("Syn Medella")) return "Syn_Medella";
            if (nazwa.contains("Żelazny Władca")) return "Żelazny_Władca";
        }
        // Emhyry (Nilfgaard)
        else if (nazwa.contains("Emhyr")) {
            if (nazwa.contains("Pan południa")) return "Pan_południa";
            if (nazwa.contains("Cesarz Nilfgaardu")) return "Cesarz_Nilfgaardu";
            if (nazwa.contains("Jeż z Erlenwaldu")) return "Jeż_z_Erlenwaldu";
            if (nazwa.contains("Najeźdźca Północy")) return "Najeźdźca_Północy";
            if (nazwa.contains("Biały Płomień")) return "Biały_Płomień";
        }
        else if (nazwa.contains("Francesca")) {
            if (nazwa.contains("Elfka czystej krwi")) return "Elfka_czystej_krwi";
            if (nazwa.contains("Stokrotka z Dolin")) return "Stokrotka_z_Dolin";
            if (nazwa.contains("Nadzieja Dol Blathanna")) return "Nadzieje_Dol_Blathanna";
            if (nazwa.contains("Królowa Dol Blathanna")) return "Krolowa_Dol_Blathanna";
            if (nazwa.contains("Najpiękniejsza kobieta na świecie")) return "Najpiekniejsza_kobieta";
        }
        else if (nazwa.contains("Eredin")) {
            if (nazwa.contains("Dowódca Czerwonych Jeźdźców")) return "Dowódca_Czerwonych_Jeźdźców";
            if (nazwa.contains("Zdradziecki")) return "Zdradziecki";
            if (nazwa.contains("Władca Tir Ná Lia")) return "Władca_Tir_Ná_Lia";
            if (nazwa.contains("Zabójca Auberona")) return "Zabójca_Auberona";
            if (nazwa.contains("Król Dzikiego Gonu")) return "Król_Dzikiego_Gonu";
        }
        return "";
    }


    public Gra(List<Karta> reka1, List<Karta> reka2, int pierwszyGracz,
               Karta dowodcaGracz1, Karta dowodcaGracz2,
               List<Karta> talia1, List<Karta> talia2,
               String nazwaTalii1, String nazwaTalii2) {
        this.rekaGracz1 = new ArrayList<>(reka1);
        this.rekaGracz2 = new ArrayList<>(reka2);
        this.aktualnyGracz = pierwszyGracz + 1;
        this.dowodcaGracz1 = dowodcaGracz1;
        this.dowodcaGracz2 = dowodcaGracz2;
        this.taliaGracza1 = new ArrayList<>(talia1);
        this.taliaGracza2 = new ArrayList<>(talia2);
        this.taliaNazwaGracza1 = nazwaTalii1 != null ? nazwaTalii1 : "";
        this.taliaNazwaGracza2 = nazwaTalii2 != null ? nazwaTalii2 : "";
        this.umiejetnoscTaliiGracza1 = przypiszUmiejetnoscTalii(nazwaTalii1);
        this.umiejetnoscTaliiGracza2 = przypiszUmiejetnoscTalii(nazwaTalii2);
        System.out.println("Gracz 1 talia: " + nazwaTalii1 + " -> umiejętność: " + umiejetnoscTaliiGracza1);
        System.out.println("Gracz 2 talia: " + nazwaTalii2 + " -> umiejętność: " + umiejetnoscTaliiGracza2);
        if (dowodcaGracz1 != null) {
            if (dowodcaGracz1.getNazwa().contains("Biały Płomień")) {
                emhyrBiałyPłomieńAktywny = true;
                System.out.println("Gracz 1: Aktywowano pasywną umiejętność - Biały Płomień blokuje dowódcę przeciwnika!");
            }
            if (dowodcaGracz1.getNazwa().contains("Najeźdźca Północy")) {
                emhyrNajeźdźcaAktywny = true;
                System.out.println("Gracz 1: Aktywowano pasywną umiejętność - Najeźdźca Północy (losowe wskrzeszanie)!");
            }
        }

        if (dowodcaGracz2 != null) {
            if (dowodcaGracz2.getNazwa().contains("Biały Płomień")) {
                emhyrBiałyPłomieńAktywny = true;
                System.out.println("Gracz 2: Aktywowano pasywną umiejętność - Biały Płomień blokuje dowódcę przeciwnika!");
            }
            if (dowodcaGracz2.getNazwa().contains("Najeźdźca Północy")) {
                emhyrNajeźdźcaAktywny = true;
                System.out.println("Gracz 2: Aktywowano pasywną umiejętność - Najeźdźca Północy (losowe wskrzeszanie)!");
            }
        }
        if (dowodcaGracz1 != null) {
            if (dowodcaGracz1.getNazwa().contains("Stokrotka z Dolin")) {
                System.out.println("Gracz 1: Aktywowano pasywną umiejętność - Stokrotka z Dolin (rozpoczyna z 11 kartami)!");
            }
        }
        if (dowodcaGracz2 != null) {
            if (dowodcaGracz2.getNazwa().contains("Stokrotka z Dolin")) {
                System.out.println("Gracz 2: Aktywowano pasywną umiejętność - Stokrotka z Dolin (rozpoczyna z 11 kartami)!");
            }
        }
        if (dowodcaGracz1 != null && dowodcaGracz1.getNazwa().contains("Zdradziecki")) {
            System.out.println("Gracz 1: Aktywowano pasywną umiejętność - siła szpiegów podwojona!");
        }
        if (dowodcaGracz2 != null && dowodcaGracz2.getNazwa().contains("Zdradziecki")) {
            System.out.println("Gracz 2: Aktywowano pasywną umiejętność - siła szpiegów podwojona!");
        }

    }
    private String przypiszUmiejetnoscTalii(String nazwaTalii) {
        if (nazwaTalii == null || nazwaTalii.isEmpty()) return "";

        String normalized = nazwaTalii.trim().toLowerCase();

        if (normalized.contains("północy") || normalized.contains("polnocy")) {
            return "Królestwo Północy";
        }
        else if (normalized.contains("nilfgaard") || normalized.contains("nilfgard")) {
            return "Nilfgaard";
        }
        else if (normalized.contains("scoia'tael") || normalized.contains("scoia tael") ||
                normalized.contains("st") || normalized.contains("scoiatael")) {
            return "Scoia'tael";
        }
        else if (normalized.contains("potwory") || normalized.contains("monsters")) {
            return "Potwory";
        }
        return "";
    }
    private void aktywujTaliePotworow(int wygrywajacyGracz) {
        System.out.println("\nAKTYWACJA TALII: Gracz " + wygrywajacyGracz + " używa umiejętności 'Potwory'");

        // Pobierz wszystkie karty na polu bitwy gracza (nie tylko jednostki)
        List<Karta> kartyNaPoluBitwy = new ArrayList<>();
        if (wygrywajacyGracz == 1) {
            kartyNaPoluBitwy.addAll(rzadBliskiGracz1);
            kartyNaPoluBitwy.addAll(rzadSrodkowyGracz1);
            kartyNaPoluBitwy.addAll(rzadDalszyGracz1);
        } else {
            kartyNaPoluBitwy.addAll(rzadBliskiGracz2);
            kartyNaPoluBitwy.addAll(rzadSrodkowyGracz2);
            kartyNaPoluBitwy.addAll(rzadDalszyGracz2);
        }

        if (kartyNaPoluBitwy.isEmpty()) {
            System.out.println("Brak kart na polu bitwy do przetasowania!");
            return;
        }

        // Filtruj tylko karty jednostek (bez bohaterów i specjalnych)
        List<Karta> jednostkiDoPrzetasowania = kartyNaPoluBitwy.stream()
                .filter(k -> k.getTyp().equalsIgnoreCase("Jednostka"))
                .collect(Collectors.toList());

        if (jednostkiDoPrzetasowania.isEmpty()) {
            System.out.println("Brak jednostek na polu bitwy do przetasowania!");
            return;
        }

        // Przetasuj i wybierz jedną kartę do zachowania
        Collections.shuffle(jednostkiDoPrzetasowania);
        Karta wylosowanaKarta = jednostkiDoPrzetasowania.get(0);
        wylosowanaKarta.setCzyZachowana(true);

        System.out.println("Wylosowano kartę: " + wylosowanaKarta.getNazwa() +
                " (Siła: " + wylosowanaKarta.getSila() + ") - zostaje na polu bitwy");

        // Reszta kart zostanie usunięta w standardowym procesie czyszczenia planszy
        // Flaga czyZachowana zostanie sprawdzona w wyczyscPlansze()
    }
    private void aktywujBraterstwo(Karta zagranaKarta, List<Karta> rzadZagrania) {
        // Pobierz pełną talię gracza (bez dowódcy)
        List<Karta> taliaGracza = (aktualnyGracz == 1) ? taliaGracza1 : taliaGracza2;
        List<Karta> rekaGracza = (aktualnyGracz == 1) ? rekaGracz1 : rekaGracz2;

        // Znajdź wszystkie pasujące karty
        List<Karta> kartyBraterstwa = PomocnikBraterstwa.znajdzKartyBraterstwa(zagranaKarta, taliaGracza);

        if (!kartyBraterstwa.isEmpty()) {
            System.out.println("Aktywacja Braterstwa dla karty " + zagranaKarta.getNazwa() + ":");

            for (Karta karta : kartyBraterstwa) {
                // Znajdź odpowiedni rząd dla przywoływanej karty
                List<Karta> odpowiedniRzad = znajdzRzadDlaGracza(karta.getPozycja(), aktualnyGracz);

                // Dodaj kartę do odpowiedniego rzędu
                odpowiedniRzad.add(karta);
                System.out.println(" - Przywołano kartę: " + karta.getNazwa() + " do rzędu: " + karta.getPozycja());

                // Usuń z ręki lub talii
                rekaGracza.remove(karta);
                if (aktualnyGracz == 1) {
                    taliaGracza1.remove(karta);
                } else {
                    taliaGracza2.remove(karta);
                }
            }
        }
    }
    public void rozpocznijGre() {
        Scanner scanner = new Scanner(System.in);

        while (zetonyZyciaGracz1 > 0 && zetonyZyciaGracz2 > 0) {
            System.out.println("\nRozpoczyna się nowa runda!");

            gracz1Pasowal = false;
            gracz2Pasowal = false;

            while (!czyKoniecRundy()) {
                wyswietlPlansze();

                if ((aktualnyGracz == 1 && gracz1Pasowal) || (aktualnyGracz == 2 && gracz2Pasowal)) {
                    System.out.println("Gracz " + aktualnyGracz + " już spasował i nie może wykonywać kolejnych ruchów.");
                    nastepnaTura();
                    continue;
                }

                System.out.println("\nGracz " + aktualnyGracz + ", wybierz kartę do zagrania (lub 'pas'):");
                String wybor = scanner.nextLine();

                if (wybor.equalsIgnoreCase("pas")) {
                    System.out.println("Gracz " + aktualnyGracz + " spasował!");
                    if (aktualnyGracz == 1) {
                        gracz1Pasowal = true;
                    } else {
                        gracz2Pasowal = true;
                    }
                    if (gracz1Pasowal && gracz2Pasowal) {
                        break;
                    }
                    nastepnaTura();
                    continue;
                }
                if (wybor.equalsIgnoreCase("dowódca")) {
                    aktywujDowodce(aktualnyGracz);
                    continue;
                }

                try {
                    int numerKarty = Integer.parseInt(wybor) - 1;
                    zagrywajKarte(numerKarty);
                } catch (Exception e) {
                    System.out.println("Błędny wybór! Spróbuj ponownie.");
                }
            }

            int punktyGracz1 = obliczPunkty(1);
            int punktyGracz2 = obliczPunkty(2);

            System.out.println("\nWynik rundy:");
            System.out.println("Gracz 1: " + punktyGracz1 + " punktów");
            System.out.println("Gracz 2: " + punktyGracz2 + " punktów");

            if (punktyGracz1 > punktyGracz2) {
                zetonyZyciaGracz2--;
                System.out.println("Gracz 1 wygrywa rundę! Gracz 2 traci żeton życia.");

                if ("Królestwo Północy".equals(umiejetnoscTaliiGracza1)) {
                    System.out.println("AKTYWACJA TALII: Gracz 1 używa umiejętności 'Królestwo Północy'");
                    aktywujTalieKrolestwaPolnocy(1);
                }
                else if ("Potwory".equals(umiejetnoscTaliiGracza1)) {
                    aktywujTaliePotworow(1); // Dodane wywołanie dla Potworów
                }
            }
            else if (punktyGracz2 > punktyGracz1) {
                zetonyZyciaGracz1--;
                System.out.println("Gracz 2 wygrywa rundę! Gracz 1 traci żeton życia.");

                if ("Królestwo Północy".equals(umiejetnoscTaliiGracza2)) {
                    System.out.println("AKTYWACJA TALII: Gracz 2 używa umiejętności 'Królestwo Północy'");
                    aktywujTalieKrolestwaPolnocy(2);
                }
                else if ("Potwory".equals(umiejetnoscTaliiGracza2)) {
                    aktywujTaliePotworow(2); // Dodane wywołanie dla Potworów
                }
            }
            else { // Remis
                System.out.println("Wynik rundy: Remis!");

                boolean gracz1Nilfgaard = "Nilfgaard".equals(umiejetnoscTaliiGracza1);
                boolean gracz2Nilfgaard = "Nilfgaard".equals(umiejetnoscTaliiGracza2);

                if (gracz1Nilfgaard && !gracz2Nilfgaard) {
                    // Tylko gracz 1 ma Nilfgaard
                    zetonyZyciaGracz2--;
                    System.out.println("AKTYWACJA TALII: Gracz 1 używa umiejętności 'Nilfgaard' - remis to zwycięstwo!");
                    System.out.println("Gracz 1 wygrywa rundę przez umiejętność talii! Gracz 2 traci żeton życia.");

                    if ("Królestwo Północy".equals(umiejetnoscTaliiGracza1)) {
                        aktywujTalieKrolestwaPolnocy(1);
                    }
                }
                else if (gracz2Nilfgaard && !gracz1Nilfgaard) {
                    // Tylko gracz 2 ma Nilfgaard
                    zetonyZyciaGracz1--;
                    System.out.println("AKTYWACJA TALII: Gracz 2 używa umiejętności 'Nilfgaard' - remis to zwycięstwo!");
                    System.out.println("Gracz 2 wygrywa rundę przez umiejętność talii! Gracz 1 traci żeton życia.");

                    if ("Królestwo Północy".equals(umiejetnoscTaliiGracza2)) {
                        aktywujTalieKrolestwaPolnocy(2);
                    }
                }
                else {
                    // Standardowy remis (oba Nilfgaardy lub żaden)
                    System.out.println("Standardowy remis! Oboje gracze tracą żeton życia.");
                    zetonyZyciaGracz1--;
                    zetonyZyciaGracz2--;

                    // Dodatkowa logika gdy obaj mają Nilfgaard
                    if (gracz1Nilfgaard && gracz2Nilfgaard) {
                        System.out.println("UWAGA: Obaj gracze mają Nilfgaard - umiejętności się znoszą!");
                    }
                }
            }


            wyczyscPlansze();
        }

        if (zetonyZyciaGracz1 <= 0 && zetonyZyciaGracz2 <= 0) {
            System.out.println("\nRemis! Oboje gracze stracili wszystkie żetony życia.");
        } else if (zetonyZyciaGracz1 <= 0) {
            System.out.println("\nGracz 2 wygrywa grę!");
        } else {
            System.out.println("\nGracz 1 wygrywa grę!");
        }
    }

    public void zagrywajKarte(int indeks) {
        List<Karta> reka = (aktualnyGracz == 1) ? rekaGracz1 : rekaGracz2;

        if (indeks < 0 || indeks >= reka.size()) {
            System.out.println("Niepoprawny numer karty!");
            return;
        }

        Karta karta = reka.remove(indeks);

        // Obsługa kart pogodowych
        if (karta.getNazwa().equalsIgnoreCase("Ulewny deszcz") ||
                karta.getNazwa().equalsIgnoreCase("Trzaskający mróz") ||
                karta.getNazwa().equalsIgnoreCase("Gęsta mgła")) {

            kartyPogodowe.add(karta);
            System.out.println("Gracz " + aktualnyGracz + " zagrał kartę pogodową: " + karta.getNazwa());
            nastepnaTura();
            return;
        }
        else if (karta.getNazwa().equalsIgnoreCase("Czyste niebo")) {
            kartyPogodowe.clear();
            System.out.println("Gracz " + aktualnyGracz + " zagrał Czyste niebo - usunięto wszystkie efekty pogodowe!");
            nastepnaTura();
            return;
        }
        else if (karta.getNazwa().equalsIgnoreCase("Róg dowódcy")) {
            System.out.println("Wybierz rząd, na który chcesz zagrać kartę Róg dowódcy:");
            System.out.println("1. Bliskie starcie");
            System.out.println("2. Jednostki strzeleckie");
            System.out.println("3. Machiny oblężnicze");

            Scanner scanner = new Scanner(System.in);
            int wyborRzedu = scanner.nextInt();

            String wybranaPozycja = "";
            switch (wyborRzedu) {
                case 1:
                    wybranaPozycja = "Bliskie starcie";
                    break;
                case 2:
                    wybranaPozycja = "Jednostki strzeleckie";
                    break;
                case 3:
                    wybranaPozycja = "Oblężnicze";
                    break;
                default:
                    System.out.println("Niepoprawny wybór rzędu!");
                    reka.add(karta);
                    return;
            }

            // Utwórz kopię karty z nową pozycją
            Karta kartaRogu = new Karta(
                    karta.getId(),
                    karta.getNazwa(),
                    karta.getTyp(),
                    karta.getTalia(),
                    karta.getSila(),
                    karta.getUmiejetnosc(),
                    karta.getUmiejetnosc_2(),
                    wybranaPozycja,
                    karta.getPozycja_2(),
                    karta.getGrafika()
            );

            // Dodaj do wybranego rzędu używając nowej metody
            dodajKarteDoRzedu(kartaRogu, wybranaPozycja, aktualnyGracz);

            // Aktywuj umiejętność
            aktywujUmiejetnosci(kartaRogu);
        }
        else if (karta.getNazwa().equalsIgnoreCase("Manekin do ćwiczeń")) {
            System.out.println("Wybierz kartę, którą chcesz podmienić z Manekinem do ćwiczeń:");

            List<Karta> poleGry = (aktualnyGracz == 1) ?
                    Stream.of(rzadBliskiGracz1, rzadSrodkowyGracz1, rzadDalszyGracz1)
                            .flatMap(List::stream)
                            .collect(Collectors.toList()) :
                    Stream.of(rzadBliskiGracz2, rzadSrodkowyGracz2, rzadDalszyGracz2)
                            .flatMap(List::stream)
                            .collect(Collectors.toList());

            int numerKarty = 1;
            for (Karta k : poleGry) {
                System.out.println(numerKarty + ". " + k.getNazwa() + " (Siła: " + obliczRzeczywistaSile(k) + ")");
                numerKarty++;
            }

            Scanner scanner = new Scanner(System.in);
            int wyborKarty = scanner.nextInt() - 1;

            if (wyborKarty >= 0 && wyborKarty < poleGry.size()) {
                Karta kartaDoPodmiany = poleGry.get(wyborKarty);
                usunKarteZRzedu(kartaDoPodmiany);

                // Użyj nowej metody do dodania karty
                dodajKarteDoRzedu(karta, kartaDoPodmiany.getPozycja(), aktualnyGracz);

                reka.add(kartaDoPodmiany);
                System.out.println("Podmieniono kartę " + kartaDoPodmiany.getNazwa() + " z Manekinem do ćwiczeń.");
            } else {
                System.out.println("Niepoprawny wybór karty!");
                reka.add(karta);
            }
        }
        else if (karta.getNazwa().equalsIgnoreCase("Pożoga")) {
            System.out.println("Gracz " + aktualnyGracz + " używa karty Pożoga!");

            List<Karta> najsilniejszeKarty = znajdzNajsilniejszeKarty();

            if (!najsilniejszeKarty.isEmpty()) {
                for (Karta k : najsilniejszeKarty) {
                    System.out.println("Zniszczono kartę " + k.getNazwa() + " (Siła: " + k.getSila() + ")");
                    if (aktualnyGracz == 1) {
                        cmentarzGracz1.add(k);
                    } else {
                        cmentarzGracz2.add(k);
                    }
                    usunKarteZRzedu(k);
                }
            } else {
                System.out.println("Nie ma kart do zniszczenia!");
            }
        }
        else if (karta.getNazwa().equalsIgnoreCase("Jaskier")) {
            List<Karta> wybranyRzad = znajdzRzad(karta);
            if (wybranyRzad != null) {
                dodajKarteDoRzedu(karta, karta.getPozycja(), aktualnyGracz);
                // Aktywuj efekt rogu dowódcy
                aktywujRogDowodcy(karta);
            }
        }
        else if (karta.maUmiejetnosc("Pożoga_K")) {
            // Najpierw dodaj kartę do odpowiedniego rzędu
            List<Karta> wybranyRzad = znajdzRzad(karta);
            if (wybranyRzad != null) {
                dodajKarteDoRzedu(karta, karta.getPozycja(), aktualnyGracz);
                // Następnie aktywuj umiejętność
                aktywujUmiejetnosci(karta);
            } else {
                System.out.println("Błąd! Nie można zagrać tej karty.");
                reka.add(karta);
            }
        }
        else {
            List<Karta> wybranyRzad = znajdzRzad(karta);
            if (wybranyRzad != null) {
                if (karta.maUmiejetnosc("Szpiegostwo")) {
                    // Dla Szpiega - wywołaj umiejętność, ale nie dodawaj karty do własnego rzędu
                    aktywujUmiejetnosci(karta);
                }
                else if (karta.maUmiejetnosc("Zmartwychwstanie")) {
                    // Dla Zmartwychwstania - wywołaj umiejętność, ale nie dodawaj karty do rzędu
                    aktywujUmiejetnosci(karta);
                }
                else if (karta.maUmiejetnosc("Więź")) {
                    dodajKarteDoRzedu(karta, karta.getPozycja(), aktualnyGracz);
                    aktywujUmiejetnosci(karta);
                }
                else if (karta.maUmiejetnosc("Wysokie morale")) {
                    dodajKarteDoRzedu(karta, karta.getPozycja(), aktualnyGracz);
                    aktywujUmiejetnosci(karta);
                }
                else {
                    // Standardowe zagranie karty
                    dodajKarteDoRzedu(karta, karta.getPozycja(), aktualnyGracz);
                    System.out.println("Gracz " + aktualnyGracz + " zagrał kartę " + karta.getNazwa());
                    if (karta.getUmiejetnosc().equals("Braterstwo")) {
                        aktywujBraterstwo(karta, wybranyRzad);
                    }
                }
            } else {
                System.out.println("Błąd! Nie można zagrać tej karty.");
                reka.add(karta);
            }
        }

        nastepnaTura();
    }


    private Karta znajdzNajsilniejszaKarte() {
        Karta najsilniejszaKarta = null;
        int maxSila = -1;

        for (List<Karta> rzad : Arrays.asList(
                rzadBliskiGracz1, rzadSrodkowyGracz1, rzadDalszyGracz1,
                rzadBliskiGracz2, rzadSrodkowyGracz2, rzadDalszyGracz2)) {
            for (Karta k : rzad) {
                if (k.getSila() > maxSila && !k.getTyp().equalsIgnoreCase("Bohater")) {
                    maxSila = k.getSila();
                    najsilniejszaKarta = k;
                }
            }
        }

        return najsilniejszaKarta;
    }

    private void usunKarteZRzedu(Karta karta) {
        boolean bylaWysokieMorale = karta.maUmiejetnosc("Wysokie morale");
        boolean bylRogDowodcy = karta.maUmiejetnosc("Róg dowódcy");
        if (rzadBliskiGracz1.remove(karta)) {
            aktualizujWiezPoUsunieciu(karta);
            if (bylaWysokieMorale) zmniejszMorale(karta);
            if (bylRogDowodcy) przywrocSileBezRogu(karta);
        }
        else if (rzadSrodkowyGracz1.remove(karta)) {
            aktualizujWiezPoUsunieciu(karta);
            if (bylaWysokieMorale) zmniejszMorale(karta);
            if (bylRogDowodcy) przywrocSileBezRogu(karta);
        }
        else if (rzadDalszyGracz1.remove(karta)) {
            aktualizujWiezPoUsunieciu(karta);
            if (bylaWysokieMorale) zmniejszMorale(karta);
            if (bylRogDowodcy) przywrocSileBezRogu(karta);
        }
        else if (rzadBliskiGracz2.remove(karta)) {
            aktualizujWiezPoUsunieciu(karta);
            if (bylaWysokieMorale) zmniejszMorale(karta);
            if (bylRogDowodcy) przywrocSileBezRogu(karta);
        }
        else if (rzadSrodkowyGracz2.remove(karta)) {
            aktualizujWiezPoUsunieciu(karta);
            if (bylaWysokieMorale) zmniejszMorale(karta);
            if (bylRogDowodcy) przywrocSileBezRogu(karta);
        }
        else {
            rzadDalszyGracz2.remove(karta);
            aktualizujWiezPoUsunieciu(karta);
            if (bylaWysokieMorale) zmniejszMorale(karta);
            if (bylRogDowodcy) przywrocSileBezRogu(karta);
        }
    }
    private void przywrocSileBezRogu(Karta rog) {
        List<Karta> rzad = znajdzRzadDlaGracza(rog.getPozycja(),
                (aktualnyGracz == 1) ? 1 : 2);

        // Sprawdź czy jest jeszcze jakiś inny efekt rogu w rzędzie
        boolean innyRogIstnieje = rzad.stream()
                .anyMatch(k -> (k.maUmiejetnosc("Róg dowódcy") ||
                        k.getNazwa().equalsIgnoreCase("Jaskier")) &&
                        k != rog);

        if (innyRogIstnieje) {
            System.out.println("Efekt Rogu pozostaje aktywny dzięki innej karcie");
            return;
        }

        // Przywróć siły tylko jeśli to ostatni róg/Jaskier w rzędzie
        for (Karta k : rzad) {
            if (k.getTyp().equalsIgnoreCase("Jednostka")) {
                int bazowaSila = znajdzBazowaSile(k);
                k.setSila(bazowaSila);
            }
        }

        String efektOd = rog.getNazwa().equalsIgnoreCase("Jaskier") ? "Jaskra" : "Rogu Dowódcy";
        System.out.println("Usunięto efekt " + efektOd + ". Siła jednostek w rzędzie " +
                rog.getPozycja() + " wróciła do wartości bazowych.");
    }
    private void zmniejszMorale(Karta karta) {
        List<Karta> rzad = znajdzRzadDlaGracza(karta.getPozycja(),
                (aktualnyGracz == 1) ? 1 : 2);

        for (Karta k : rzad) {
            if (k != karta) { // Nie modyfikuj siebie samej
                k.setSila(k.getSila() - 1);
            }
        }

        System.out.println("Usunięto efekt Wysokiego Morale. Karty w rzędzie " +
                karta.getPozycja() + " straciły 1 punkt siły.");
    }

    private void dodajKarteDoRzedu(Karta karta, String pozycja, int gracz) {
        List<Karta> rzad = znajdzRzadDlaGracza(pozycja, gracz);
        rzad.add(karta);

        // Sprawdzamy efekty dowódców
        Map<String, Integer> efekty = gracz == 1 ? efektyDowodcowGracz1 : efektyDowodcowGracz2;

        if (efekty.containsKey("Zdobywca") && pozycja.equalsIgnoreCase("Oblężnicze")) {
            // Sprawdź czy jest już róg dowódcy w rzędzie (pomijając aktualną kartę)
            boolean jestRog = rzad.stream()
                    .anyMatch(k -> (k.maUmiejetnosc("Róg dowódcy") ||
                            k.getNazwa().equalsIgnoreCase("Jaskier")) &&
                            k != karta);

            if (!jestRog && karta.getTyp().equalsIgnoreCase("Jednostka")) {
                int bazowaSila = znajdzBazowaSile(karta);
                karta.setSila(bazowaSila * efekty.get("Zdobywca"));
                System.out.println("Efekt Foltesta Zdobywcy: Siła karty " + karta.getNazwa() +
                        " została pomnożona przez " + efekty.get("Zdobywca"));
            }
        }
    }

    private int obliczRzeczywistaSile(Karta karta) {
        // Efekty pogodowe
        for (Karta pogoda : kartyPogodowe) {
            if (pogoda.getNazwa().equalsIgnoreCase("Ulewny deszcz") &&
                    karta.getPozycja().equalsIgnoreCase("Oblężnicze") &&
                    !karta.getTyp().equalsIgnoreCase("Bohater")) {
                return 1;
            }
            if (pogoda.getNazwa().equalsIgnoreCase("Trzaskający mróz") &&
                    karta.getPozycja().equalsIgnoreCase("Bliskie starcie") &&
                    !karta.getTyp().equalsIgnoreCase("Bohater")) {
                return 1;
            }
            if (pogoda.getNazwa().equalsIgnoreCase("Gęsta mgła") &&
                    karta.getPozycja().equalsIgnoreCase("Jednostki strzeleckie") &&
                    !karta.getTyp().equalsIgnoreCase("Bohater")) {
                return 1;
            }
        }

        int sila = karta.getSila();

        // Efekt rogu dowódcy/Jaskra
        if (karta.getTyp().equalsIgnoreCase("Jednostka")) {
            List<Karta> rzad = znajdzRzadDlaPozycji(karta.getPozycja(), karta);
            boolean jestRog = rzad.stream()
                    .anyMatch(k -> (k.maUmiejetnosc("Róg dowódcy") ||
                            k.getNazwa().equalsIgnoreCase("Jaskier")) &&
                            !k.equals(karta));

            if (jestRog) {
                sila *= 2;
            }
        }
        if ((dowodcaGracz1 != null && dowodcaGracz1.getNazwa().contains("Zdradziecki") &&
                (rzadBliskiGracz1.contains(karta) || rzadSrodkowyGracz1.contains(karta) || rzadDalszyGracz1.contains(karta))) ||
                (dowodcaGracz2 != null && dowodcaGracz2.getNazwa().contains("Zdradziecki") &&
                        (rzadBliskiGracz2.contains(karta) || rzadSrodkowyGracz2.contains(karta) || rzadDalszyGracz2.contains(karta)))) {
            if (karta.maUmiejetnosc("Szpiegostwo")) {
                sila *= 2;
            }
        }

        return sila;
    }


    private List<Karta> znajdzRzadDlaPozycji(String pozycja, Karta karta) {
        if (rzadBliskiGracz1.contains(karta) || rzadSrodkowyGracz1.contains(karta) || rzadDalszyGracz1.contains(karta)) {
            if (pozycja.equalsIgnoreCase("Bliskie starcie")) return rzadBliskiGracz1;
            if (pozycja.equalsIgnoreCase("Jednostki strzeleckie")) return rzadSrodkowyGracz1;
            if (pozycja.equalsIgnoreCase("Oblężnicze")) return rzadDalszyGracz1;
        }
        else if (rzadBliskiGracz2.contains(karta) || rzadSrodkowyGracz2.contains(karta) || rzadDalszyGracz2.contains(karta)) {
            if (pozycja.equalsIgnoreCase("Bliskie starcie")) return rzadBliskiGracz2;
            if (pozycja.equalsIgnoreCase("Jednostki strzeleckie")) return rzadSrodkowyGracz2;
            if (pozycja.equalsIgnoreCase("Oblężnicze")) return rzadDalszyGracz2;
        }
        else {
            if (pozycja.equalsIgnoreCase("Bliskie starcie")) return aktualnyGracz == 1 ? rzadBliskiGracz1 : rzadBliskiGracz2;
            if (pozycja.equalsIgnoreCase("Jednostki strzeleckie")) return aktualnyGracz == 1 ? rzadSrodkowyGracz1 : rzadSrodkowyGracz2;
            if (pozycja.equalsIgnoreCase("Oblężnicze")) return aktualnyGracz == 1 ? rzadDalszyGracz1 : rzadDalszyGracz2;
        }
        return new ArrayList<>();
    }
    private List<Karta> znajdzRzad(Karta karta) {
        // Tylko karty ze Zręcznością mają wybór pozycji
        if (karta.maUmiejetnosc("Zręczność") &&
                karta.getPozycja_2() != null &&
                !karta.getPozycja_2().equalsIgnoreCase("Brak")) {

            System.out.println("Wybierz pozycję dla karty " + karta.getNazwa() + ":");
            System.out.println("1. " + karta.getPozycja());
            System.out.println("2. " + karta.getPozycja_2());

            Scanner scanner = new Scanner(System.in);
            int wybor = scanner.nextInt();
            scanner.nextLine(); // Czyść bufer

            if (wybor == 2) {
                // Aktualizujemy pozycję karty przed dodaniem do rzędu
                karta.setPozycja(karta.getPozycja_2());
                return znajdzRzadDlaPozycji(karta.getPozycja_2());
            }
        }
        return znajdzRzadDlaPozycji(karta.getPozycja());
    }

    private List<Karta> znajdzRzadDlaPozycji(String pozycja) {
        if (pozycja.equalsIgnoreCase("Bliskie starcie")) {
            return new ArrayList<>(rzadBliskiGracz1);
        } else if (pozycja.equalsIgnoreCase("Jednostki strzeleckie")) {
            return new ArrayList<>(rzadSrodkowyGracz1);
        } else if (pozycja.equalsIgnoreCase("Oblężnicze")) {
            return new ArrayList<>(rzadDalszyGracz1);
        }
        return new ArrayList<>();
    }
    private void nastepnaTura() {
        aktualnyGracz = (aktualnyGracz == 1) ? 2 : 1;
    }

    public void wyswietlPlansze() {
        System.out.println("\nPLANSZA");

        // Wyświetlanie informacji o graczu 1
        System.out.println("Żetony życia Gracza 1: (" + zetonyZyciaGracz1 + "/2)");
        if (dowodcaGracz1 != null) {
            String statusDowodcy1 = dowodcaUzytyGracz1 ? " (wykorzystany)" : " (dostępny)";
            System.out.println("Dowódca Gracza 1: " + dowodcaGracz1.getNazwa() + statusDowodcy1);

            // Dodatkowa informacja o pasywnych umiejętnościach
            if (dowodcaGracz1.getNazwa().contains("Biały Płomień")) {
                System.out.println("  [Pasywna umiejętność: Blokada dowódcy przeciwnika]");
            }
            if (dowodcaGracz1.getNazwa().contains("Najeźdźca Północy") && emhyrNajeźdźcaAktywny) {
                System.out.println("  [Pasywna umiejętność: Wskrzeszanie losowe]");
            }
        } else {
            System.out.println("Dowódca Gracza 1: Brak");
        }

        // Wyświetlanie cmentarza i talii gracza 1
        System.out.println("\nCmentarz Gracza 1: " + wyswietlKarty(cmentarzGracz1));
        System.out.println("Karty w talii Gracza 1: " + taliaGracza1.size());

        // Wyświetlanie rzędów gracza 1
        wyswietlRzad("\nMachiny oblężnicze", rzadDalszyGracz1);
        wyswietlRzad("Jednostki strzeleckie", rzadSrodkowyGracz1);
        wyswietlRzad("Bliskie starcie", rzadBliskiGracz1);

        System.out.println("----------------------------------------");
        System.out.println("Karty pogodowe: " + wyswietlKarty(kartyPogodowe));
        System.out.println("----------------------------------------");

        // Wyświetlanie rzędów gracza 2
        wyswietlRzad("Bliskie starcie", rzadBliskiGracz2);
        wyswietlRzad("Jednostki strzeleckie", rzadSrodkowyGracz2);
        wyswietlRzad("Machiny oblężnicze", rzadDalszyGracz2);

        // Wyświetlanie informacji o graczu 2
        System.out.println("\nŻetony życia Gracza 2: (" + zetonyZyciaGracz2 + "/2)");
        System.out.println("Cmentarz Gracza 2: " + wyswietlKarty(cmentarzGracz2));
        System.out.println("Karty w talii Gracza 2: " + taliaGracza2.size());

        if (dowodcaGracz2 != null) {
            String statusDowodcy2 = dowodcaUzytyGracz2 ? " (wykorzystany)" : " (dostępny)";
            System.out.println("Dowódca Gracza 2: " + dowodcaGracz2.getNazwa() + statusDowodcy2);

            if (dowodcaGracz2.getNazwa().contains("Biały Płomień")) {
                System.out.println("  [Pasywna umiejętność: Blokada dowódcy przeciwnika]");
            }
            if (dowodcaGracz2.getNazwa().contains("Najeźdźca Północy") && emhyrNajeźdźcaAktywny) {
                System.out.println("  [Pasywna umiejętność: Wskrzeszanie losowe]");
            }
        } else {
            System.out.println("Dowódca Gracza 2: Brak");
        }

        // Wyświetlanie ręki aktualnego gracza
        System.out.println("\nRęka Gracza " + aktualnyGracz + ":");
        List<Karta> reka = (aktualnyGracz == 1) ? rekaGracz1 : rekaGracz2;
        for (int i = 0; i < reka.size(); i++) {
            Karta karta = reka.get(i);
            String umiejetnosc = karta.getUmiejetnosc().equalsIgnoreCase("Brak") ? "" :
                    " (Umiejętność: " + karta.getUmiejetnosc() + ")";
            System.out.println((i + 1) + ". " + karta.getNazwa() +
                    " | Siła: " + karta.getSila() +
                    " | Pozycja: " + karta.getPozycja() +
                    umiejetnosc);
        }
    }    private void aktywujDowodce(int gracz) {
        // Sprawdzenie czy przeciwnik ma aktywnego Emhyra "Biały Płomień"
        int przeciwnik = gracz == 1 ? 2 : 1;
        Karta przeciwnikDowodca = przeciwnik == 1 ? dowodcaGracz1 : dowodcaGracz2;

        // Sprawdzenie pasywnej umiejętności Biały Płomień
        if (przeciwnikDowodca != null && przeciwnikDowodca.getNazwa().contains("Biały Płomień")) {
            System.out.println("Umiejętność dowódcy zablokowana przez pasywną umiejętność Emhyra Biały Płomień!");
            return;
        }

        // Sprawdzenie czy dowódca był już używany
        if ((gracz == 1 && dowodcaUzytyGracz1) || (gracz == 2 && dowodcaUzytyGracz2)) {
            System.out.println("Umiejętność dowódcy została już użyta w tej grze!");
            return;
        }

        Karta aktualnyDowodca = (gracz == 1) ? dowodcaGracz1 : dowodcaGracz2;

        // Sprawdź czy to Stokrotka z Dolin (umiejętność pasywna)
        if (aktualnyDowodca != null && aktualnyDowodca.getNazwa().contains("Stokrotka z Dolin")) {
            System.out.println("Francesca Stokrotka z Dolin ma umiejętność pasywną - nie można jej aktywować!");
            return;
        }

        String typDowodcy = sprawdzDowodce(gracz);
        if (typDowodcy.isEmpty()) {
            System.out.println("Gracz " + gracz + " nie ma aktywnego dowódcy!");
            return;
        }

        // Pomijamy pasywne umiejętności w aktywacji
        if (typDowodcy.equals("Biały_Płomień") || typDowodcy.equals("Najeźdźca_Północy")) {
            System.out.println("Ta umiejętność jest pasywna i działa przez całą grę!");
            return;
        }

        System.out.println("Gracz " + gracz + " aktywuje umiejętność dowódcy: " + typDowodcy.replace("_", " "));

        // Obsługa tylko aktywnych umiejętności
        switch (typDowodcy) {
            case "Zdobywca":
                aktywujFoltestaZdobywce(gracz);
                break;
            case "Dowódca_Północy":
                aktywujFoltestaDowodcePolnocy(gracz);
                break;
            case "Król_Temerii":
                aktywujFoltestaKrolaTemerii(gracz);
                break;
            case "Syn_Medella":
                aktywujFoltestaSynaMedella(gracz);
                break;
            case "Żelazny_Władca":
                aktywujFoltestaZelaznegoWladce(gracz);
                break;
            case "Pan_południa":
                aktywujEmhyraPanaPoludnia(gracz);
                break;
            case "Cesarz_Nilfgaardu":
                aktywujEmhyraCesarza(gracz);
                break;
            case "Jeż_z_Erlenwaldu":
                aktywujEmhyraJeza(gracz);
                break;
            case "Najeźdźca_Północy":
                // Tylko rejestracja że umiejętność jest aktywna (efekt pasywny)
                emhyrNajeźdźcaAktywny = true;
                System.out.println("Emhyr Najeźdźca Północy: Aktywowano pasywną umiejętność!");
                System.out.println("Wskrzeszanie jednostek będzie teraz losowe.");
                break;
            // Biały_Płomień jest pominięty - to umiejętność pasywna
            case "Elfka_czystej_krwi":
                aktywujFrancesceElfka(gracz);
                break;
            case "Stokrotka_z_Dolin":
                // Ta umiejętność jest pasywna - nie powinna być tutaj wywoływana
                break;
            case "Nadzieje_Dol_Blathanna":
                aktywujFrancesceNadzieje(gracz);
                break;
            case "Krolowa_Dol_Blathanna":
                aktywujFrancesceKrolowa(gracz);
                break;
            case "Najpiekniejsza_kobieta":
                aktywujFrancesceNajpiekniejsza(gracz);
                break;
            case "Dowódca_Czerwonych_Jeźdźców":
                aktywujEredinaDowodceCzerwonychJezdzcow(gracz);
                break;
            case "Zdradziecki":
                System.out.println("Umiejętność pasywna - siła szpiegów jest podwojona!");
                break;
            case "Władca_Tir_Ná_Lia":
                aktywujEredinaWladceTirNaLia(gracz);
                break;
            case "Zabójca_Auberona":
                aktywujEredinaZabojceAuberona(gracz);
                break;
            case "Król_Dzikiego_Gonu":
                aktywujEredinaKrolaDzikiegoGonu(gracz);
                break;
        }

        if (gracz == 1) {
            dowodcaUzytyGracz1 = true;
        } else {
            dowodcaUzytyGracz2 = true;
        }

        // Zmiana tury tylko dla aktywnych umiejętności
        // Nie zmieniamy tury dla pasywnych umiejętności (Najeźdźca_Północy, Biały_Płomień, Stokrotka_z_Dolin)
        if (!typDowodcy.equals("Najeźdźca_Północy") &&
                !typDowodcy.equals("Biały_Płomień") &&
                !typDowodcy.equals("Stokrotka_z_Dolin")) {
            nastepnaTura();
        }
    }
    private void aktywujFrancesceElfka(int gracz) {
        Karta trzaskajacyMroz = znajdzKartePoNazwie(gracz, "Trzaskający mróz");

        if (trzaskajacyMroz != null) {
            kartyPogodowe.add(trzaskajacyMroz);
            System.out.println("Francesca Elfka czystej krwi: Zagrano kartę 'Trzaskający mróz'!");
            aktywujEfektPogodowy("Trzaskający mróz");
        } else {
            System.out.println("Francesca Elfka czystej krwi: Brak karty 'Trzaskający mróz' w talii!");
        }
    }
    private void aktywujFrancesceStokrotka(int gracz) {
        System.out.println("Francesca Stokrotka z Dolin: Umiejętność pasywna - gracz rozpoczyna z 11 kartami zamiast 10");
    }
    private void aktywujFrancesceNadzieje(int gracz) {
        int przesunieteKarty = 0;

        List<Karta> kartyGracza = new ArrayList<>();
        kartyGracza.addAll(znajdzRzadDlaGracza("Bliskie starcie", gracz));
        kartyGracza.addAll(znajdzRzadDlaGracza("Jednostki strzeleckie", gracz));
        kartyGracza.addAll(znajdzRzadDlaGracza("Oblężnicze", gracz));

        List<Karta> kartyZRzecznoscia = kartyGracza.stream()
                .filter(k -> k.maUmiejetnosc("Zręczność") &&
                        k.getPozycja_2() != null &&
                        !k.getPozycja_2().equalsIgnoreCase("Brak"))
                .collect(Collectors.toList());

        for (Karta karta : kartyZRzecznoscia) {
            String obecnaPozycja = karta.getPozycja();
            String alternatywnaPozycja = karta.getPozycja_2();

            int obecnaSila = obliczRzeczywistaSile(karta);

            String tempPozycja = karta.getPozycja();
            karta.setPozycja(alternatywnaPozycja);
            int alternatywnaSila = obliczRzeczywistaSile(karta);
            karta.setPozycja(tempPozycja);

            if (alternatywnaSila > obecnaSila) {
                znajdzRzadDlaGracza(obecnaPozycja, gracz).remove(karta);
                znajdzRzadDlaGracza(alternatywnaPozycja, gracz).add(karta);
                karta.setPozycja(alternatywnaPozycja);
                przesunieteKarty++;
            }
        }

        System.out.println("Francesca Nadzieja Dol Blathanna: Przesunięto " + przesunieteKarty + " kart(y) do optymalnych rzędów!");
    }
    private void aktywujFrancesceKrolowa(int gracz) {
        int przeciwnik = (gracz == 1) ? 2 : 1;
        List<Karta> rzadPrzeciwnika = znajdzRzadDlaGracza("Bliskie starcie", przeciwnik);
        List<Karta> cmentarz = (przeciwnik == 1) ? cmentarzGracz1 : cmentarzGracz2;

        int sumaSily = rzadPrzeciwnika.stream()
                .mapToInt(this::obliczRzeczywistaSile)
                .sum();

        if (sumaSily >= 10) {
            int maxSila = rzadPrzeciwnika.stream()
                    .mapToInt(this::obliczRzeczywistaSile)
                    .max()
                    .orElse(0);

            List<Karta> doZniszczenia = rzadPrzeciwnika.stream().filter(k -> obliczRzeczywistaSile(k) == maxSila).collect(Collectors.toList());

            rzadPrzeciwnika.removeAll(doZniszczenia);
            cmentarz.addAll(doZniszczenia);

            System.out.println("Francesca Królowa Dol Blathanna: Zniszczono " + doZniszczenia.size() +
                    " kart(y) o sile " + maxSila + " w rzędzie Bliskie starcie!");
        } else {
            System.out.println("Francesca Królowa Dol Blathanna: Suma sił w rzędzie Bliskie starcie jest mniejsza niż 10!");
        }
    }
    private void aktywujFrancesceNajpiekniejsza(int gracz) {
        List<Karta> rzadStrzelcow = znajdzRzadDlaGracza("Jednostki strzeleckie", gracz);

        // Sprawdź czy jest już róg dowódcy w rzędzie
        boolean jestRog = rzadStrzelcow.stream()
                .anyMatch(k -> k.maUmiejetnosc("Róg dowódcy") || k.getNazwa().equalsIgnoreCase("Jaskier"));

        if (!jestRog) {
            for (Karta karta : rzadStrzelcow) {
                if (karta.getTyp().equalsIgnoreCase("Jednostka")) {
                    int bazowaSila = znajdzBazowaSile(karta);
                    karta.setSila(bazowaSila * 2);
                }
            }
            System.out.println("Francesca Najpiękniejsza kobieta: Podwojono siłę jednostek w rzędzie strzelców!");
        } else {
            System.out.println("Francesca Najpiękniejsza kobieta: Efekt nie zadziałał - w rzędzie jest już róg dowódcy!");
        }
    }

    private void aktywujEmhyraPanaPoludnia(int gracz) {
        int przeciwnik = gracz == 1 ? 2 : 1;
        List<Karta> cmentarz = przeciwnik == 1 ? cmentarzGracz1 : cmentarzGracz2;
        List<Karta> reka = gracz == 1 ? rekaGracz1 : rekaGracz2;

        if (cmentarz.isEmpty()) {
            System.out.println("Cmentarz przeciwnika jest pusty!");
            return;
        }

        System.out.println("Wybierz kartę z cmentarza przeciwnika:");
        for (int i = 0; i < cmentarz.size(); i++) {
            System.out.println((i + 1) + ". " + cmentarz.get(i).getNazwa());
        }

        Scanner scanner = new Scanner(System.in);
        int wybor = scanner.nextInt() - 1;
        scanner.nextLine(); // Czyść bufor

        if (wybor >= 0 && wybor < cmentarz.size()) {
            Karta wybranaKarta = cmentarz.remove(wybor);
            reka.add(wybranaKarta);
            System.out.println("Dodano kartę " + wybranaKarta.getNazwa() + " do ręki!");
        } else {
            System.out.println("Nieprawidłowy wybór!");
        }
    }

    private void aktywujEmhyraCesarza(int gracz) {
        int przeciwnik = gracz == 1 ? 2 : 1;
        List<Karta> rekaPrzeciwnika = przeciwnik == 1 ? rekaGracz1 : rekaGracz2;

        if (rekaPrzeciwnika.isEmpty()) {
            System.out.println("Przeciwnik nie ma kart na ręce!");
            return;
        }

        System.out.println("Trzy losowe karty z ręki przeciwnika:");
        Random random = new Random();
        for (int i = 0; i < Math.min(3, rekaPrzeciwnika.size()); i++) {
            int index = random.nextInt(rekaPrzeciwnika.size());
            Karta karta = rekaPrzeciwnika.get(index);
            System.out.println((i + 1) + ". " + karta.getNazwa() + " | Siła: " + karta.getSila());
        }
    }

    private void aktywujEmhyraJeza(int gracz) {
        // Szukaj karty "Ulewny deszcz" we wszystkich możliwych miejscach (z tolerancją dla białych znaków)
        Karta ulewnyDeszcz = znajdzKartePoNazwie(gracz, "Ulewny deszcz");

        if (ulewnyDeszcz != null) {
            // Przenieś kartę do efektów pogodowych
            kartyPogodowe.add(ulewnyDeszcz);
            System.out.println("Emhyr Jeż z Erlenwaldu: Zagrano kartę 'Ulewny deszcz'!");

            // Aktywuj efekt pogodowy (nieważne gdzie karta była znaleziona)
            aktywujEfektPogodowy("Ulewny deszcz");
        } else {
            System.out.println("Emhyr Jeż z Erlenwaldu: Brak karty 'Ulewny deszcz' w dostępnych kartach!");
        }
    }

    // Pomocnicza metoda do znajdowania kart z tolerancją dla białych znaków
    private Karta znajdzKartePoNazwie(int gracz, String nazwa) {
        String nazwaNormalized = nazwa.trim().toLowerCase();

        // Sprawdź talię
        List<Karta> talia = (gracz == 1) ? taliaGracza1 : taliaGracza2;
        for (Karta k : talia) {
            if (k.getNazwa().trim().toLowerCase().equals(nazwaNormalized)) {
                talia.remove(k);
                return k;
            }
        }

        // Sprawdź rękę
        List<Karta> reka = (gracz == 1) ? rekaGracz1 : rekaGracz2;
        for (Karta k : reka) {
            if (k.getNazwa().trim().toLowerCase().equals(nazwaNormalized)) {
                reka.remove(k);
                return k;
            }
        }

        // Sprawdź karty specjalne
        List<Karta> specjalne = (gracz == 1) ? kartySpecjalneGracz1 : kartySpecjalneGracz2;
        for (Karta k : specjalne) {
            if (k.getNazwa().trim().toLowerCase().equals(nazwaNormalized)) {
                specjalne.remove(k);
                return k;
            }
        }

        return null;
    }

    // Metoda aktywująca efekt pogodowy
    private void aktywujEfektPogodowy(String nazwaPogody) {
        switch(nazwaPogody.trim().toLowerCase()) {
            case "ulewny deszcz":
                System.out.println("EFEKTY: Wszystkie machiny oblężnicze mają teraz siłę 1!");
                break;
            case "trzaskający mróz":
                System.out.println("EFEKTY: Wszystkie karty wręcz mają teraz siłę 1!");
                break;
            case "gęsta mgła":
                System.out.println("EFEKTY: Wszystkie karty dystansowe mają teraz siłę 1!");
                break;
        }
    }
    private void aktywujEredinaDowodceCzerwonychJezdzcow(int gracz) {
        List<Karta> rzadBliski = znajdzRzadDlaGracza("Bliskie starcie", gracz);

        // Sprawdź czy jest już róg dowódcy w rzędzie
        boolean jestRog = rzadBliski.stream()
                .anyMatch(k -> k.maUmiejetnosc("Róg dowódcy") || k.getNazwa().equalsIgnoreCase("Jaskier"));

        if (!jestRog) {
            for (Karta karta : rzadBliski) {
                if (karta.getTyp().equalsIgnoreCase("Jednostka")) {
                    int bazowaSila = znajdzBazowaSile(karta);
                    karta.setSila(bazowaSila * 2);
                }
            }
            System.out.println("Eredin Dowódca Czerwonych Jeźdźców: Podwojono siłę jednostek w rzędzie bliskiego starcia!");
        } else {
            System.out.println("Eredin Dowódca Czerwonych Jeźdźców: Efekt nie zadziałał - w rzędzie jest już róg dowódcy!");
        }
    }
    private void aktywujEredinaKrolaDzikiegoGonu(int gracz) {
        List<Karta> talia = (gracz == 1) ? taliaGracza1 : taliaGracza2;
        List<Karta> kartyPogodoweWTalii = talia.stream()
                .filter(k -> k.getNazwa().equalsIgnoreCase("Ulewny deszcz") ||
                        k.getNazwa().equalsIgnoreCase("Trzaskający mróz") ||
                        k.getNazwa().equalsIgnoreCase("Gęsta mgła") ||
                        k.getNazwa().equalsIgnoreCase("Czyste niebo"))
                .collect(Collectors.toList());

        if (kartyPogodoweWTalii.isEmpty()) {
            System.out.println("Brak kart pogodowych w talii!");
            return;
        }

        System.out.println("Wybierz kartę pogodową:");
        for (int i = 0; i < kartyPogodoweWTalii.size(); i++) {
            System.out.println((i + 1) + ". " + kartyPogodoweWTalii.get(i).getNazwa());
        }

        Scanner scanner = new Scanner(System.in);
        try {
            int wybor = scanner.nextInt() - 1;
            if (wybor >= 0 && wybor < kartyPogodoweWTalii.size()) {
                Karta wybranaPogoda = kartyPogodoweWTalii.get(wybor);
                talia.remove(wybranaPogoda);
                kartyPogodowe.add(wybranaPogoda);
                System.out.println("Zagrano kartę pogodową: " + wybranaPogoda.getNazwa());
                aktywujEfektPogodowy(wybranaPogoda.getNazwa());
            } else {
                System.out.println("Nieprawidłowy wybór!");
            }
        } catch (Exception e) {
            System.out.println("Błędny wybór!");
        }
    }
    private void aktywujEredinaZabojceAuberona(int gracz) {
        List<Karta> cmentarz = (gracz == 1) ? cmentarzGracz1 : cmentarzGracz2;
        List<Karta> reka = (gracz == 1) ? rekaGracz1 : rekaGracz2;

        if (cmentarz.isEmpty()) {
            System.out.println("Cmentarz jest pusty!");
            return;
        }

        System.out.println("Wybierz kartę z cmentarza:");
        for (int i = 0; i < cmentarz.size(); i++) {
            System.out.println((i + 1) + ". " + cmentarz.get(i).getNazwa());
        }

        Scanner scanner = new Scanner(System.in);
        try {
            int wybor = scanner.nextInt() - 1;
            if (wybor >= 0 && wybor < cmentarz.size()) {
                Karta wybranaKarta = cmentarz.remove(wybor);
                reka.add(wybranaKarta);
                System.out.println("Dodano do ręki: " + wybranaKarta.getNazwa());
            } else {
                System.out.println("Nieprawidłowy wybór!");
            }
        } catch (Exception e) {
            System.out.println("Błędny wybór!");
        }
    }
    private void aktywujEredinaWladceTirNaLia(int gracz) {
        List<Karta> reka = (gracz == 1) ? rekaGracz1 : rekaGracz2;
        List<Karta> talia = (gracz == 1) ? taliaGracza1 : taliaGracza2;
        List<Karta> cmentarz = (gracz == 1) ? cmentarzGracz1 : cmentarzGracz2;

        if (reka.size() < 2) {
            System.out.println("Za mało kart na ręce (wymagane 2)!");
            return;
        }

        if (talia.isEmpty()) {
            System.out.println("Talia jest pusta!");
            return;
        }

        // Wybór 2 kart z ręki
        System.out.println("Wybierz 2 karty z ręki do oddania:");
        for (int i = 0; i < reka.size(); i++) {
            System.out.println((i + 1) + ". " + reka.get(i).getNazwa());
        }

        Scanner scanner = new Scanner(System.in);
        try {
            System.out.print("Wybierz pierwszą kartę: ");
            int wybor1 = scanner.nextInt() - 1;
            System.out.print("Wybierz drugą kartę: ");
            int wybor2 = scanner.nextInt() - 1;

            if (wybor1 >= 0 && wybor1 < reka.size() &&
                    wybor2 >= 0 && wybor2 < reka.size() &&
                    wybor1 != wybor2) {

                // Wybór karty z talii
                System.out.println("Wybierz kartę z talii:");
                for (int i = 0; i < talia.size(); i++) {
                    System.out.println((i + 1) + ". " + talia.get(i).getNazwa());
                }
                System.out.print("Twój wybór: ");
                int wyborKarty = scanner.nextInt() - 1;

                if (wyborKarty >= 0 && wyborKarty < talia.size()) {
                    // Wymiana kart
                    Karta karta1 = reka.remove(Math.max(wybor1, wybor2));
                    Karta karta2 = reka.remove(Math.min(wybor1, wybor2));
                    Karta wybranaKarta = talia.remove(wyborKarty);

                    cmentarz.add(karta1);
                    cmentarz.add(karta2);
                    reka.add(wybranaKarta);

                    System.out.println("Wymieniono " + karta1.getNazwa() + " i " + karta2.getNazwa() +
                            " na " + wybranaKarta.getNazwa() + " z talii");
                } else {
                    System.out.println("Nieprawidłowy wybór karty z talii!");
                }
            } else {
                System.out.println("Nieprawidłowy wybór kart z ręki!");
            }
        } catch (Exception e) {
            System.out.println("Błędny wybór!");
        }
    }
    private void aktywujEmhyraNajezdzce(int gracz) {
        emhyrNajeźdźcaAktywny = true;
        System.out.println("Emhyr Najeźdźca Północy: Aktywowano pasywną umiejętność!");
        System.out.println("Wskrzeszanie jednostek będzie teraz losowe zamiast z wyboru.");
    }

    private void aktywujEmhyraBialyPlomien(int gracz) {
        emhyrBiałyPłomieńAktywny = true;
        System.out.println("Emhyr Biały Płomień: Aktywowano pasywną umiejętność!");
        System.out.println("Umiejętność dowódcy przeciwnika jest zablokowana!");
    }
    private void aktywujFoltestaZdobywce(int gracz) {
        Map<String, Integer> efekty = gracz == 1 ? efektyDowodcowGracz1 : efektyDowodcowGracz2;
        efekty.put("Zdobywca", 2); // Współczynnik mnożenia siły

        List<Karta> rzadOblezenia = znajdzRzadDlaGracza("Oblężnicze", gracz);

        // Sprawdź czy jest już róg dowódcy w rzędzie
        boolean jestRog = rzadOblezenia.stream()
                .anyMatch(k -> k.maUmiejetnosc("Róg dowódcy") || k.getNazwa().equalsIgnoreCase("Jaskier"));

        if (!jestRog) {
            for (Karta karta : rzadOblezenia) {
                if (karta.getTyp().equalsIgnoreCase("Jednostka")) {
                    int bazowaSila = znajdzBazowaSile(karta);
                    karta.setSila(bazowaSila * 2);
                }
            }
            System.out.println("Foltest Zdobywca: Podwojono siłę wszystkich jednostek w rzędzie oblężniczym!");
            System.out.println("Nowe karty dodane do tego rzędu również będą miały podwojoną siłę!");
        } else {
            System.out.println("Foltest Zdobywca: Efekt nie zadziałał - w rzędzie jest już aktywny róg dowódcy!");
            efekty.remove("Zdobywca"); // Usuwamy efekt jeśli nie może być aktywny
        }
    }

    private void aktywujFoltestaSynaMedella(int gracz) {
        int przeciwnik = gracz == 1 ? 2 : 1;
        List<Karta> rzadStrzelcow = znajdzRzadDlaGracza("Jednostki strzeleckie", przeciwnik);

        int sumaSily = rzadStrzelcow.stream()
                .mapToInt(this::obliczRzeczywistaSile)
                .sum();

        if (sumaSily >= 10) {
            OptionalInt maxSila = rzadStrzelcow.stream()
                    .mapToInt(Karta::getSila)
                    .max();

            if (maxSila.isPresent()) {
                List<Karta> doZniszczenia = rzadStrzelcow.stream()
                        .filter(k -> k.getSila() == maxSila.getAsInt())
                        .collect(Collectors.toList());

                List<Karta> cmentarz = przeciwnik == 1 ? cmentarzGracz1 : cmentarzGracz2;
                doZniszczenia.forEach(cmentarz::add);
                rzadStrzelcow.removeAll(doZniszczenia);

                System.out.println("Foltest Syn Medella: Zniszczono " + doZniszczenia.size() +
                        " najsilniejszych jednostek przeciwnika w rzędzie strzelców!");
            }
        } else {
            System.out.println("Foltest Syn Medella: Suma sił w rzędzie strzelców przeciwnika jest mniejsza niż 10!");
        }
    }

    private void aktywujFoltestaKrolaTemerii(int gracz) {
        List<Karta> talia = gracz == 1 ? taliaGracza1 : taliaGracza2;
        Optional<Karta> gestaMgla = talia.stream()
                .filter(k -> k.getNazwa().equalsIgnoreCase("Gęsta mgła"))
                .findFirst();

        if (gestaMgla.isPresent()) {
            Karta karta = gestaMgla.get();
            talia.remove(karta);
            kartyPogodowe.add(karta);
            System.out.println("Foltest Król Temerii: Zagrano kartę Gęsta mgła z talii!");
        } else {
            System.out.println("Foltest Król Temerii: Brak karty Gęsta mgła w talii!");
        }
    }

    private void aktywujFoltestaDowodcePolnocy(int gracz) {
        if (!kartyPogodowe.isEmpty()) {
            kartyPogodowe.clear();
            System.out.println("Foltest Dowódca Północy: Usunięto wszystkie efekty pogodowe!");
        } else {
            System.out.println("Foltest Dowódca Północy: Brak aktywnych efektów pogodowych!");
        }
    }

    private void aktywujFoltestaZelaznegoWladce(int gracz) {
        int przeciwnik = gracz == 1 ? 2 : 1;
        List<Karta> rzadOblezenia = znajdzRzadDlaGracza("Oblężnicze", przeciwnik);

        int sumaSily = rzadOblezenia.stream()
                .mapToInt(this::obliczRzeczywistaSile)
                .sum();

        if (sumaSily >= 10) {
            OptionalInt maxSila = rzadOblezenia.stream()
                    .mapToInt(Karta::getSila)
                    .max();

            if (maxSila.isPresent()) {
                List<Karta> doZniszczenia = rzadOblezenia.stream()
                        .filter(k -> k.getSila() == maxSila.getAsInt())
                        .collect(Collectors.toList());

                List<Karta> cmentarz = przeciwnik == 1 ? cmentarzGracz1 : cmentarzGracz2;
                doZniszczenia.forEach(cmentarz::add);
                rzadOblezenia.removeAll(doZniszczenia);

                System.out.println("Foltest Żelazny Władca: Zniszczono " + doZniszczenia.size() +
                        " najsilniejszych jednostek przeciwnika w rzędzie oblężniczym!");
            }
        } else {
            System.out.println("Foltest Żelazny Władca: Suma sił w rzędzie oblężniczym przeciwnika jest mniejsza niż 10!");
        }
    }

    private void wyswietlRzad(String nazwa, List<Karta> rzad) {
        int sumaSily = rzad.stream().mapToInt(this::obliczRzeczywistaSile).sum();
        System.out.print(nazwa + " (Siła: " + sumaSily + "): ");

        if (rzad.isEmpty()) {
            System.out.println("(pusto)");
        } else {
            for (Karta k : rzad) {
                int rzeczywistaSila = obliczRzeczywistaSile(k);
                System.out.print("[" + k.getNazwa() + " (" + rzeczywistaSila + ")] ");
            }
            System.out.println();
        }
    }

    private String wyswietlKarty(List<Karta> karty) {
        if (karty.isEmpty()) return "Brak";
        return karty.stream()
                .map(Karta::getNazwa)
                .collect(Collectors.joining("], [", "[", "]"));
    }

    public void wyczyscPlansze() {
        // Przenosimy karty gracza 1 na jego cmentarz (tylko te, które NIE są zachowane)
        przeniesKartyNaCmentarz(rzadBliskiGracz1, cmentarzGracz1);
        przeniesKartyNaCmentarz(rzadSrodkowyGracz1, cmentarzGracz1);
        przeniesKartyNaCmentarz(rzadDalszyGracz1, cmentarzGracz1);

        // Przenosimy karty gracza 2 na jego cmentarz (tylko te, które NIE są zachowane)
        przeniesKartyNaCmentarz(rzadBliskiGracz2, cmentarzGracz2);
        przeniesKartyNaCmentarz(rzadSrodkowyGracz2, cmentarzGracz2);
        przeniesKartyNaCmentarz(rzadDalszyGracz2, cmentarzGracz2);

        // Czyszczenie rzędów (zachowane karty pozostają)
        rzadBliskiGracz1.removeIf(k -> !k.isCzyZachowana());
        rzadSrodkowyGracz1.removeIf(k -> !k.isCzyZachowana());
        rzadDalszyGracz1.removeIf(k -> !k.isCzyZachowana());
        rzadBliskiGracz2.removeIf(k -> !k.isCzyZachowana());
        rzadSrodkowyGracz2.removeIf(k -> !k.isCzyZachowana());
        rzadDalszyGracz2.removeIf(k -> !k.isCzyZachowana());

        // Resetowanie flag zachowania dla następnej rundy
        rzadBliskiGracz1.forEach(k -> k.setCzyZachowana(false));
        rzadSrodkowyGracz1.forEach(k -> k.setCzyZachowana(false));
        rzadDalszyGracz1.forEach(k -> k.setCzyZachowana(false));
        rzadBliskiGracz2.forEach(k -> k.setCzyZachowana(false));
        rzadSrodkowyGracz2.forEach(k -> k.setCzyZachowana(false));
        rzadDalszyGracz2.forEach(k -> k.setCzyZachowana(false));

        // Karty specjalne i pogodowe są usuwane
        kartySpecjalneGracz1.clear();
        kartySpecjalneGracz2.clear();

        if (zetonyZyciaGracz1 <= 0 || zetonyZyciaGracz2 <= 0) {
            kartyPogodowe.clear();
            efektyDowodcowGracz1.clear();
            efektyDowodcowGracz2.clear();
            dowodcaUzytyGracz1 = false;
            dowodcaUzytyGracz2 = false;
            emhyrNajeźdźcaAktywny = false;
            emhyrBiałyPłomieńAktywny = false;
        }
    }

    // Nowa metoda pomocnicza
    private void przeniesKartyNaCmentarz(List<Karta> rzad, List<Karta> cmentarz) {
        Iterator<Karta> iterator = rzad.iterator();
        while (iterator.hasNext()) {
            Karta karta = iterator.next();
            if (karta.getTyp().equalsIgnoreCase("Jednostka") && !karta.isCzyZachowana()) {
                cmentarz.add(karta);
                iterator.remove();
            }
        }
    }

    public int obliczPunkty(int gracz) {
        List<Karta> rzadBliski = (gracz == 1) ? rzadBliskiGracz1 : rzadBliskiGracz2;
        List<Karta> rzadSrodkowy = (gracz == 1) ? rzadSrodkowyGracz1 : rzadSrodkowyGracz2;
        List<Karta> rzadDalszy = (gracz == 1) ? rzadDalszyGracz1 : rzadDalszyGracz2;

        return rzadBliski.stream().mapToInt(this::obliczRzeczywistaSile).sum() +
                rzadSrodkowy.stream().mapToInt(this::obliczRzeczywistaSile).sum() +
                rzadDalszy.stream().mapToInt(this::obliczRzeczywistaSile).sum();
    }

    private boolean czyKoniecRundy() {
        return (gracz1Pasowal && gracz2Pasowal) ||
                (rekaGracz1.isEmpty() && rekaGracz2.isEmpty()) ||
                (rekaGracz1.isEmpty() && gracz2Pasowal) ||
                (rekaGracz2.isEmpty() && gracz1Pasowal);
    }

    private void aktywujUmiejetnosci(Karta karta) {
        // Aktywuj pierwszą umiejętność
        if (!karta.getUmiejetnosc().equalsIgnoreCase("Brak")) {
            aktywujUmiejetnosc(karta, karta.getUmiejetnosc());
        }

        // Aktywuj drugą umiejętność jeśli istnieje
        if (karta.getUmiejetnosc_2() != null && !karta.getUmiejetnosc_2().equalsIgnoreCase("Brak")) {
            aktywujUmiejetnosc(karta, karta.getUmiejetnosc_2());
        }
    }

    private void aktywujUmiejetnosc(Karta karta, String umiejetnosc) {
        switch (umiejetnosc.toLowerCase()) {
            case "szpiegostwo":
                aktywujSzpiegostwo(karta);
                break;
            case "zmartwychwstanie":
                aktywujZmartwychwstanie(karta);
                break;
            case "więź":
                aktywujWiez(karta);
                break;
            case "wysokie morale":
                aktywujWysokieMorale(karta);
                break;
            case "róg dowódcy":
                aktywujRogDowodcy(karta);
                break;
            case "pożoga_k":
                aktywujPozogeK(karta);
                break;
        }
    }
    private void aktywujPozogeK(Karta karta) {
        System.out.println("\n=== Aktywacja Pożogi przez Gracza " + aktualnyGracz + " ===");

        // 1. Znajdź odpowiedni rząd przeciwnika
        int przeciwnik = (aktualnyGracz == 1) ? 2 : 1;
        List<Karta> rzadPrzeciwnika = znajdzRzadDlaGracza(karta.getPozycja(), przeciwnik);

        // 2. Oblicz sumę sił w rzędzie przeciwnika
        int sumaSily = rzadPrzeciwnika.stream()
                .mapToInt(this::obliczRzeczywistaSile)
                .sum();

        System.out.println("Suma sił w rzędzie przeciwnika (" + karta.getPozycja() + "): " + sumaSily);

        if (sumaSily <= 10) {
            System.out.println("Pożoga_K nie zadziała - suma sił nie przekracza 10.");
            return;
        }

        // 3. Znajdź najsilniejszą jednostkę (pomijając bohaterów)
        Optional<Karta> najsilniejszaJednostka = rzadPrzeciwnika.stream()
                .filter(k -> k.getTyp().equalsIgnoreCase("Jednostka"))
                .max(Comparator.comparingInt(Karta::getSila));

        if (!najsilniejszaJednostka.isPresent()) {
            System.out.println("Brak jednostek do zniszczenia w rzędzie przeciwnika.");
            return;
        }

        int silaDoZniszczenia = najsilniejszaJednostka.get().getSila();
        System.out.println("Najsilniejsza jednostka w rzędzie: " +
                najsilniejszaJednostka.get().getNazwa() + " (Siła: " + silaDoZniszczenia + ")");

        // 4. Znajdź wszystkie jednostki o tej samej sile
        List<Karta> kartyDoZniszczenia = rzadPrzeciwnika.stream()
                .filter(k -> k.getTyp().equalsIgnoreCase("Jednostka"))
                .filter(k -> k.getSila() == silaDoZniszczenia)
                .collect(Collectors.toList());

        if (kartyDoZniszczenia.isEmpty()) {
            System.out.println("Błąd: Nie znaleziono jednostek do zniszczenia.");
            return;
        }

        // 5. Zniszcz karty i przenieś na cmentarz przeciwnika
        List<Karta> cmentarzPrzeciwnika = (przeciwnik == 1) ? cmentarzGracz1 : cmentarzGracz2;

        // Musimy użyć iteratora, aby bezpiecznie usuwać podczas iteracji
        Iterator<Karta> iterator = rzadPrzeciwnika.iterator();
        while (iterator.hasNext()) {
            Karta k = iterator.next();
            if (kartyDoZniszczenia.contains(k)) {
                System.out.println("Zniszczono: " + k.getNazwa() + " (Siła: " + k.getSila() + ")");
                cmentarzPrzeciwnika.add(k);
                iterator.remove();
            }
        }

        System.out.println("Zniszczono " + kartyDoZniszczenia.size() +
                " kart(y) o sile " + silaDoZniszczenia);
    }
    private void aktywujRogDowodcy(Karta rog) {
        List<Karta> rzad = znajdzRzadDlaGracza(rog.getPozycja(), aktualnyGracz);

        // Sprawdź czy w rzędzie już jest aktywny efekt rogu (Jaskier lub inny róg)
        boolean juzIstniejeEfektRogu = rzad.stream()
                .anyMatch(k -> (k.maUmiejetnosc("Róg dowódcy") ||
                        k.getNazwa().equalsIgnoreCase("Jaskier")) &&
                        k != rog);

        if (juzIstniejeEfektRogu) {
            System.out.println("Efekt Rogu Dowódcy już jest aktywny w tym rzędzie (dzięki Jaskrowi lub innemu rogowi)");
            return;
        }

        // Podwój siłę wszystkich jednostek w rzędzie
        for (Karta k : rzad) {
            if (k != rog && k.getTyp().equalsIgnoreCase("Jednostka")) {
                int bazowaSila = znajdzBazowaSile(k);
                k.setSila(bazowaSila * 2);
            }
        }

        String efektOd = rog.getNazwa().equalsIgnoreCase("Jaskier") ? "Jaskra" : "Rogu Dowódcy";
        System.out.println("Aktywowano efekt " + efektOd + "! Siła jednostek w rzędzie " +
                rog.getPozycja() + " została podwojona.");
    }
    private void aktywujWysokieMorale(Karta karta) {
        List<Karta> rzad = znajdzRzadDlaGracza(karta.getPozycja(), aktualnyGracz);

        for (Karta k : rzad) {
            if (k != karta) { // Nie modyfikuj siebie samej
                k.setSila(k.getSila() + 1);
            }
        }

        System.out.println("Aktywowano Wysokie Morale! Wszystkie karty w rzędzie " + karta.getPozycja() +
                " (oprócz siebie) zyskały +1 do siły.");
    }
    private void aktywujWiez(Karta nowaKarta) {
        List<Karta> rzad = znajdzRzadDlaGracza(nowaKarta.getPozycja(), aktualnyGracz);

        // Znajdź tylko zagrane karty z więzią o tej samej nazwie w TYM rzędzie
        List<Karta> kartyZWiezia = rzad.stream()
                .filter(k -> k.getNazwa().equals(nowaKarta.getNazwa())
                        && k.maUmiejetnosc("Więź")
                        && k != nowaKarta) // Wyklucz nową kartę
                .collect(Collectors.toList());

        int bazowaSila = nowaKarta.getSila(); // Pobierz bazową siłę nowej karty
        int liczbaKart = kartyZWiezia.size() + 1; // Uwzględniamy nową kartę

        // Oblicz nową siłę dla WSZYSTKICH powiązanych kart (włącznie z nową)
        int nowaSila = bazowaSila * liczbaKart;

        // Zaktualizuj siłę wszystkich powiązanych kart w rzędzie
        for (Karta k : kartyZWiezia) {
            k.setSila(nowaSila);
        }
        // Ustaw siłę nowej karty
        nowaKarta.setSila(nowaSila);

        System.out.println("Więź aktywna! " + liczbaKart + " karty " + nowaKarta.getNazwa() +
                " w rzędzie. Siła każdej: " + nowaSila + " (bazowa: " + bazowaSila + ")");
    }
    private void aktualizujWiezPoUsunieciu(Karta usunietaKarta) {
        if (!usunietaKarta.maUmiejetnosc("Więź")) return;

        List<Karta> rzad = znajdzRzadDlaGracza(usunietaKarta.getPozycja(),
                (rzadBliskiGracz1.contains(usunietaKarta) ||
                        rzadSrodkowyGracz1.contains(usunietaKarta) ||
                        rzadDalszyGracz1.contains(usunietaKarta)) ? 1 : 2);

        List<Karta> kartyZWiezia = rzad.stream()
                .filter(k -> k.getNazwa().equals(usunietaKarta.getNazwa()) &&
                        k.maUmiejetnosc("Więź"))
                .collect(Collectors.toList());

        if (kartyZWiezia.isEmpty()) return;

        int bazowaSila = znajdzBazowaSile(usunietaKarta);
        int nowaSila = bazowaSila * kartyZWiezia.size();

        for (Karta k : kartyZWiezia) {
            k.setSila(nowaSila);
        }
    }
    private int znajdzBazowaSile(Karta karta) {
        // Najpierw sprawdź w ręce
        for (Karta k : rekaGracz1) if (k.getNazwa().equals(karta.getNazwa())) return k.getSila();
        for (Karta k : rekaGracz2) if (k.getNazwa().equals(karta.getNazwa())) return k.getSila();

        // Potem w talii
        for (Karta k : taliaGracza1) if (k.getNazwa().equals(karta.getNazwa())) return k.getSila();
        for (Karta k : taliaGracza2) if (k.getNazwa().equals(karta.getNazwa())) return k.getSila();

        // Na koniec na cmentarzu
        for (Karta k : cmentarzGracz1) if (k.getNazwa().equals(karta.getNazwa())) return k.getSila();
        for (Karta k : cmentarzGracz2) if (k.getNazwa().equals(karta.getNazwa())) return k.getSila();

        return karta.getSila(); // Jeśli nie znajdziemy, zwróć aktualną siłę
    }
    private void aktywujZmartwychwstanie(Karta karta) {
        List<Karta> cmentarz = (aktualnyGracz == 1) ? cmentarzGracz1 : cmentarzGracz2;
        List<Karta> jednostkiNaCmentarzu = cmentarz.stream()
                .filter(k -> k.getTyp().equalsIgnoreCase("Jednostka"))
                .collect(Collectors.toList());

        if (jednostkiNaCmentarzu.isEmpty()) {
            System.out.println("Brak jednostek na cmentarzu - karta zostaje zagrana normalnie.");
            dodajKarteDoRzedu(karta, karta.getPozycja(), aktualnyGracz);
            return;
        }

        if (emhyrNajeźdźcaAktywny) {
            // Losowe wskrzeszenie - efekt Najeźdźcy Północy
            Karta wybranaKarta = jednostkiNaCmentarzu.get(new Random().nextInt(jednostkiNaCmentarzu.size()));
            cmentarz.remove(wybranaKarta);
            dodajKarteDoRzedu(wybranaKarta, wybranaKarta.getPozycja(), aktualnyGracz);
            System.out.println("Najeźdźca Północy: Losowo wskrzeszono " + wybranaKarta.getNazwa());
        } else {
            // Standardowe wskrzeszenie z wyborem
            System.out.println("Wybierz jednostkę do wskrzeszenia:");
            for (int i = 0; i < jednostkiNaCmentarzu.size(); i++) {
                System.out.println((i + 1) + ". " + jednostkiNaCmentarzu.get(i).getNazwa());
            }

            Scanner scanner = new Scanner(System.in);
            int wybor = scanner.nextInt() - 1;
            if (wybor >= 0 && wybor < jednostkiNaCmentarzu.size()) {
                Karta wybranaKarta = jednostkiNaCmentarzu.get(wybor);
                cmentarz.remove(wybranaKarta);
                dodajKarteDoRzedu(wybranaKarta, wybranaKarta.getPozycja(), aktualnyGracz);
                System.out.println("Wskrzeszono: " + wybranaKarta.getNazwa());
            }
        }
    }

    private void aktywujSzpiegostwo(Karta karta) {
        // 1. Umieść kartę na planszy przeciwnika
        int przeciwnik = (aktualnyGracz == 1) ? 2 : 1;
        List<Karta> rzadPrzeciwnika = znajdzRzadDlaGracza(karta.getPozycja(), przeciwnik);
        rzadPrzeciwnika.add(karta);

        // 2. Gracz dobiera 2 losowe karty z talii (pomijając dowódców)
        List<Karta> taliaGracza = (aktualnyGracz == 1) ? new ArrayList<>(taliaGracza1) : new ArrayList<>(taliaGracza2);
        List<Karta> rekaGracza = (aktualnyGracz == 1) ? rekaGracz1 : rekaGracz2;

        // Filtrujemy talię - usuwamy dowódców
        List<Karta> taliaBezDowodcow = taliaGracza.stream()
                .filter(k -> !k.getTyp().equalsIgnoreCase("Dowódca"))
                .collect(Collectors.toList());

        Random random = new Random();
        int dobraneKarty = 0;

        while (!taliaBezDowodcow.isEmpty() && dobraneKarty < 2) {
            // Losujemy kartę
            int losowyIndex = random.nextInt(taliaBezDowodcow.size());
            Karta dobranaKarta = taliaBezDowodcow.remove(losowyIndex);

            // Dodajemy do ręki
            rekaGracza.add(dobranaKarta);

            // Usuwamy z talii (oryginalnej)
            if (aktualnyGracz == 1) {
                taliaGracza1.remove(dobranaKarta);
            } else {
                taliaGracza2.remove(dobranaKarta);
            }

            dobraneKarty++;
            System.out.println("Dobrano kartę: " + dobranaKarta.getNazwa());
        }

        System.out.println("Gracz " + aktualnyGracz + " zagrał Szpiega! Karta trafia do przeciwnika, a gracz dobiera " + dobraneKarty + " karty.");
    }
    private List<Karta> znajdzRzadDlaGracza(String pozycja, int gracz) {
        if (pozycja.equalsIgnoreCase("Bliskie starcie")) {
            return gracz == 1 ? rzadBliskiGracz1 : rzadBliskiGracz2;
        } else if (pozycja.equalsIgnoreCase("Jednostki strzeleckie")) {
            return gracz == 1 ? rzadSrodkowyGracz1 : rzadSrodkowyGracz2;
        } else if (pozycja.equalsIgnoreCase("Oblężnicze")) {
            return gracz == 1 ? rzadDalszyGracz1 : rzadDalszyGracz2;
        }
        return new ArrayList<>();
    }
    private List<Karta> znajdzNajsilniejszeKarty() {
        List<Karta> wszystkieKarty = new ArrayList<>();
        wszystkieKarty.addAll(rzadBliskiGracz1);
        wszystkieKarty.addAll(rzadSrodkowyGracz1);
        wszystkieKarty.addAll(rzadDalszyGracz1);
        wszystkieKarty.addAll(rzadBliskiGracz2);
        wszystkieKarty.addAll(rzadSrodkowyGracz2);
        wszystkieKarty.addAll(rzadDalszyGracz2);

        // Filtrujemy tylko jednostki (nie bohaterów)
        List<Karta> jednostki = wszystkieKarty.stream()
                .filter(k -> !k.getTyp().equalsIgnoreCase("Bohater"))
                .collect(Collectors.toList());

        if (jednostki.isEmpty()) return new ArrayList<>();

        int maxSila = jednostki.stream()
                .mapToInt(Karta::getSila)
                .max()
                .orElse(0);

        return jednostki.stream()
                .filter(k -> k.getSila() == maxSila)
                .collect(Collectors.toList());
    }
    private void aktywujTalieKrolestwaPolnocy(int wygrywajacyGracz) {
        // Tworzymy kopie aby uniknąć modyfikacji oryginalnej listy podczas iteracji
        List<Karta> taliaGracza = (wygrywajacyGracz == 1)
                ? new ArrayList<>(taliaGracza1)
                : new ArrayList<>(taliaGracza2);
        List<Karta> rekaGracza = (wygrywajacyGracz == 1) ? rekaGracz1 : rekaGracz2;

        // Debug: wyświetl stan przed dobraniem
        System.out.println("[DEBUG] Przed dobraniem - talia: " + taliaGracza.size() +
                " karty, ręka: " + rekaGracza.size() + " kart");

        // Filtruj tylko karty jednostek (bez dowódców i specjalnych)
        List<Karta> kartyDoDobrania = new ArrayList<>();
        for (Karta karta : taliaGracza) {
            if (karta.getTyp().equalsIgnoreCase("Jednostka") ||
                    karta.getTyp().equalsIgnoreCase("Bohater")) {
                kartyDoDobrania.add(karta);
            }
        }

        if (!kartyDoDobrania.isEmpty()) {
            // Inicjalizacja generatora liczb losowych
            Random random = new Random();
            int losowyIndex = random.nextInt(kartyDoDobrania.size());
            Karta dobranaKarta = kartyDoDobrania.get(losowyIndex);

            // Debug
            System.out.println("[DEBUG] Wylosowana karta: " + dobranaKarta.getNazwa() +
                    " z " + kartyDoDobrania.size() + " dostępnych kart");

            // Dodaj do ręki i usuń z talii
            rekaGracza.add(dobranaKarta);
            if (wygrywajacyGracz == 1) {
                taliaGracza1.remove(dobranaKarta);
            } else {
                taliaGracza2.remove(dobranaKarta);
            }

            System.out.println("Gracz " + wygrywajacyGracz + " dobrał kartę: " + dobranaKarta.getNazwa());
            System.out.println("Nowa ręka (" + rekaGracza.size() + " kart):");
            for (int i = 0; i < rekaGracza.size(); i++) {
                System.out.println((i+1) + ". " + rekaGracza.get(i).getNazwa());
            }
        } else {
            System.out.println("Brak dostępnych kart jednostek w talii do dobrania!");
        }

        // Debug: wyświetl stan po dobraniu
        System.out.println("[DEBUG] Po dobraniu - talia: " +
                ((wygrywajacyGracz == 1) ? taliaGracza1.size() : taliaGracza2.size()) +
                " karty, ręka: " + rekaGracza.size() + " kart");
    }
}