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

        // Znajdź wszystkie pasujące karty
        List<Karta> kartyBraterstwa = PomocnikBraterstwa.znajdzKartyBraterstwa(zagranaKarta, taliaGracza);

        if (!kartyBraterstwa.isEmpty()) {
            System.out.println("Aktywacja Braterstwa dla karty " + zagranaKarta.getNazwa() + ":");

            for (Karta karta : kartyBraterstwa) {
                // Znajdź odpowiedni rząd dla przywoływanej karty
                List<Karta> odpowiedniRzad = znajdzRzadDlaPozycji(karta.getPozycja());

                System.out.println(" - Przywołano kartę: " + karta.getNazwa() + " do rzędu: " + karta.getPozycja());
                odpowiedniRzad.add(karta);

                // Usuń z ręki lub talii
                if (aktualnyGracz == 1) {
                    rekaGracz1.remove(karta);
                    taliaGracza1.remove(karta);
                } else {
                    rekaGracz2.remove(karta);
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
                    wybranaPozycja,  // Ustaw nową pozycję
                    karta.getPozycja_2(),
                    karta.getGrafika()
            );

            // Dodaj do wybranego rzędu
            List<Karta> wybranyRzad = znajdzRzadDlaGracza(wybranaPozycja, aktualnyGracz);
            wybranyRzad.add(kartaRogu);

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
                dodajKarteDoRzedu(karta, kartaDoPodmiany);
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
                wybranyRzad.add(karta);
                // Aktywuj efekt rogu dowódcy
                aktywujRogDowodcy(karta);
            }
        }
        else if (karta.maUmiejetnosc("Pożoga_K")) {
            // Najpierw dodaj kartę do odpowiedniego rzędu
            List<Karta> wybranyRzad = znajdzRzad(karta);
            if (wybranyRzad != null) {
                wybranyRzad.add(karta);
                // Następnie aktywuj umiejętność
                aktywujUmiejetnosci(karta);
            } else {
                System.out.println("Błąd! Nie można zagrać tej karty.");
                reka.add(karta); // Zwróć kartę do ręki
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
                    wybranyRzad.add(karta); // Najpierw dodaj do rzędu
                    aktywujUmiejetnosci(karta); // Potem aktywuj więź
                }
                else if (karta.maUmiejetnosc("Wysokie morale")) {
                    wybranyRzad.add(karta);
                    aktywujUmiejetnosci(karta);
                }
                else {
                    // Standardowe zagranie karty
                    wybranyRzad.add(karta);
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

    private void dodajKarteDoRzedu(Karta karta, Karta kartaWzor) {
        if (rzadBliskiGracz1.contains(kartaWzor)) rzadBliskiGracz1.add(karta);
        else if (rzadSrodkowyGracz1.contains(kartaWzor)) rzadSrodkowyGracz1.add(karta);
        else if (rzadDalszyGracz1.contains(kartaWzor)) rzadDalszyGracz1.add(karta);
        else if (rzadBliskiGracz2.contains(kartaWzor)) rzadBliskiGracz2.add(karta);
        else if (rzadSrodkowyGracz2.contains(kartaWzor)) rzadSrodkowyGracz2.add(karta);
        else if (rzadDalszyGracz2.contains(kartaWzor)) rzadDalszyGracz2.add(karta);
    }

    private int obliczRzeczywistaSile(Karta karta) {
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
        return karta.getSila();
    }

    private List<Karta> znajdzRzad(Karta karta) {
        // Tylko karty ze Zręcznością mają wybór pozycji
        if (karta.maUmiejetnosc("Zręczność") &&
                karta.getPozycja_2() != null &&
                !karta.getPozycja_2().equalsIgnoreCase("N/D")) {

            System.out.println("Wybierz pozycję dla karty " + karta.getNazwa() + ":");
            System.out.println("1. " + karta.getPozycja());
            System.out.println("2. " + karta.getPozycja_2());

            Scanner scanner = new Scanner(System.in);
            int wybor = scanner.nextInt();

            if (wybor == 2) {
                return znajdzRzadDlaPozycji(karta.getPozycja_2());
            }
        }
        return znajdzRzadDlaPozycji(karta.getPozycja());
    }

    private List<Karta> znajdzRzadDlaPozycji(String pozycja) {
        if (pozycja.equalsIgnoreCase("Bliskie starcie")) {
            return (aktualnyGracz == 1) ? rzadBliskiGracz1 : rzadBliskiGracz2;
        } else if (pozycja.equalsIgnoreCase("Jednostki strzeleckie")) {
            return (aktualnyGracz == 1) ? rzadSrodkowyGracz1 : rzadSrodkowyGracz2;
        } else if (pozycja.equalsIgnoreCase("Oblężnicze")) {
            return (aktualnyGracz == 1) ? rzadDalszyGracz1 : rzadDalszyGracz2;
        }
        return null;
    }

    private void nastepnaTura() {
        aktualnyGracz = (aktualnyGracz == 1) ? 2 : 1;
    }

    public void wyswietlPlansze() {
        System.out.println("\nPLANSZA");

        System.out.println("Żetony życia Gracza 1: (" + zetonyZyciaGracz1 + "/2)");
        System.out.println("Dowódca Gracza 1: " + (dowodcaGracz1 != null ? dowodcaGracz1.getNazwa() : "Brak"));

        System.out.println("\nCmentarz Gracza 1: " + wyswietlKarty(cmentarzGracz1));
        System.out.println("Karty w talii Gracza 1: " + taliaGracza1.size());

        wyswietlRzad("\nMachiny oblężnicze", rzadDalszyGracz1);
        wyswietlRzad("Jednostki strzeleckie", rzadSrodkowyGracz1);
        wyswietlRzad("Bliskie starcie", rzadBliskiGracz1);
        System.out.println("----------------------------------------");
        System.out.println("Karty pogodowe: " + wyswietlKarty(kartyPogodowe));
        System.out.println("----------------------------------------");

        wyswietlRzad("Bliskie starcie", rzadBliskiGracz2);
        wyswietlRzad("Jednostki strzeleckie", rzadSrodkowyGracz2);
        wyswietlRzad("Machiny oblężnicze", rzadDalszyGracz2);

        System.out.println("\nCmentarz Gracza 2: " + wyswietlKarty(cmentarzGracz2));
        System.out.println("Karty w talii Gracza 2: " + taliaGracza2.size());

        System.out.println("\nDowódca Gracza 2: " + (dowodcaGracz2 != null ? dowodcaGracz2.getNazwa() : "Brak"));
        System.out.println("Żetony życia Gracza 2: (" + zetonyZyciaGracz2 + "/2)");

        // Ręka aktualnego gracza
        System.out.println("\nRęka Gracza " + aktualnyGracz + ":");
        List<Karta> reka = (aktualnyGracz == 1) ? rekaGracz1 : rekaGracz2;
        for (int i = 0; i < reka.size(); i++) {
            Karta karta = reka.get(i);
            String umiejetnosc = karta.getUmiejetnosc().equalsIgnoreCase("Brak") ? "" : " (Umiejętność: " + karta.getUmiejetnosc() + ")";
            System.out.println((i + 1) + ". " + karta.getNazwa() + " | Siła: " + karta.getSila() + " | Pozycja: " + karta.getPozycja() + umiejetnosc);
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
        kartyPogodowe.clear();
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
            System.out.println("Brak jednostek na cmentarzu do wskrzeszenia - karta zostaje zagrana normalnie.");

            // Zagraj kartę normalnie do odpowiedniego rzędu
            List<Karta> wybranyRzad = znajdzRzad(karta);
            if (wybranyRzad != null) {
                wybranyRzad.add(karta);
                System.out.println("Gracz " + aktualnyGracz + " zagrał kartę " + karta.getNazwa() + " normalnie.");
            } else {
                System.out.println("Błąd! Nie można zagrać tej karty.");
                // Zwróć kartę do ręki
                if (aktualnyGracz == 1) {
                    rekaGracz1.add(karta);
                } else {
                    rekaGracz2.add(karta);
                }
            }
            return;
        }

        System.out.println("Wybierz jednostkę do wskrzeszenia:");
        for (int i = 0; i < jednostkiNaCmentarzu.size(); i++) {
            Karta jednostka = jednostkiNaCmentarzu.get(i);
            System.out.println((i + 1) + ". " + jednostka.getNazwa() +
                    " (Siła: " + jednostka.getSila() +
                    ", Pozycja: " + jednostka.getPozycja() + ")");
        }

        Scanner scanner = new Scanner(System.in);
        try {
            int wybor = scanner.nextInt() - 1;
            if (wybor >= 0 && wybor < jednostkiNaCmentarzu.size()) {
                Karta wybranaKarta = jednostkiNaCmentarzu.get(wybor);

                // Usuń z cmentarza
                cmentarz.remove(wybranaKarta);

                // Dodaj do odpowiedniego rzędu (BEZ aktywacji umiejętności)
                List<Karta> rzad = znajdzRzadDlaGracza(wybranaKarta.getPozycja(), aktualnyGracz);
                rzad.add(wybranaKarta);

                System.out.println("Wskrzeszono jednostkę: " + wybranaKarta.getNazwa() +
                        " na pozycji " + wybranaKarta.getPozycja());

                // Celowo NIE aktywujemy umiejętności wskrzeszonej karty
            } else {
                System.out.println("Nieprawidłowy wybór! Karta zostaje zagrana normalnie.");
                List<Karta> wybranyRzad = znajdzRzad(karta);
                if (wybranyRzad != null) {
                    wybranyRzad.add(karta);
                } else {
                    // Zwróć kartę do ręki
                    if (aktualnyGracz == 1) {
                        rekaGracz1.add(karta);
                    } else {
                        rekaGracz2.add(karta);
                    }
                }
            }
        } catch (InputMismatchException e) {
            System.out.println("Wprowadź poprawny numer karty! Karta zostaje zagrana normalnie.");
            List<Karta> wybranyRzad = znajdzRzad(karta);
            if (wybranyRzad != null) {
                wybranyRzad.add(karta);
            } else {
                // Zwróć kartę do ręki
                if (aktualnyGracz == 1) {
                    rekaGracz1.add(karta);
                } else {
                    rekaGracz2.add(karta);
                }
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
            return (gracz == 1) ? rzadBliskiGracz1 : rzadBliskiGracz2;
        } else if (pozycja.equalsIgnoreCase("Jednostki strzeleckie")) {
            return (gracz == 1) ? rzadSrodkowyGracz1 : rzadSrodkowyGracz2;
        } else if (pozycja.equalsIgnoreCase("Oblężnicze")) {
            return (gracz == 1) ? rzadDalszyGracz1 : rzadDalszyGracz2;
        }
        return null;
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