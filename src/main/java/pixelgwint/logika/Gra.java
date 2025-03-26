package pixelgwint.logika;

import pixelgwint.model.Karta;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    public Gra(List<Karta> reka1, List<Karta> reka2, int pierwszyGracz, Karta dowodcaGracz1, Karta dowodcaGracz2) {
        this.rekaGracz1 = new ArrayList<>(reka1);
        this.rekaGracz2 = new ArrayList<>(reka2);
        this.aktualnyGracz = pierwszyGracz + 1;
        this.dowodcaGracz1 = dowodcaGracz1;
        this.dowodcaGracz2 = dowodcaGracz2;
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
            } else if (punktyGracz2 > punktyGracz1) {
                zetonyZyciaGracz1--;
                System.out.println("Gracz 2 wygrywa rundę! Gracz 1 traci żeton życia.");
            } else {
                System.out.println("Remis! Oboje gracze tracą żeton życia.");
                zetonyZyciaGracz1--;
                zetonyZyciaGracz2--;
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

        // Prostsza obsługa kart pogodowych
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

            List<Karta> wybranyRzad = null;
            switch (wyborRzedu) {
                case 1:
                    wybranyRzad = (aktualnyGracz == 1) ? rzadBliskiGracz1 : rzadBliskiGracz2;
                    break;
                case 2:
                    wybranyRzad = (aktualnyGracz == 1) ? rzadSrodkowyGracz1 : rzadSrodkowyGracz2;
                    break;
                case 3:
                    wybranyRzad = (aktualnyGracz == 1) ? rzadDalszyGracz1 : rzadDalszyGracz2;
                    break;
                default:
                    System.out.println("Niepoprawny wybór rzędu!");
                    reka.add(karta);
                    return;
            }

            wybranyRzad.add(karta);
            System.out.println("Gracz " + aktualnyGracz + " zagrał kartę Róg dowódcy na rząd " + wyborRzedu);
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

            Karta najsilniejszaKarta = znajdzNajsilniejszaKarte();

            if (najsilniejszaKarta != null) {
                System.out.println("Zniszczono kartę " + najsilniejszaKarta.getNazwa() + " (Siła: " + najsilniejszaKarta.getSila() + ")");
                if (aktualnyGracz == 1) {
                    cmentarzGracz1.add(najsilniejszaKarta);
                } else {
                    cmentarzGracz2.add(najsilniejszaKarta);
                }
                usunKarteZRzedu(najsilniejszaKarta);
            } else {
                System.out.println("Nie ma kart do zniszczenia!");
            }
        }
        else {
            List<Karta> wybranyRzad = znajdzRzad(karta);
            if (wybranyRzad != null) {
                wybranyRzad.add(karta);
                System.out.println("Gracz " + aktualnyGracz + " zagrał kartę " + karta.getNazwa());
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
        rzadBliskiGracz1.remove(karta);
        rzadSrodkowyGracz1.remove(karta);
        rzadDalszyGracz1.remove(karta);
        rzadBliskiGracz2.remove(karta);
        rzadSrodkowyGracz2.remove(karta);
        rzadDalszyGracz2.remove(karta);
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
        if (karta.getPozycja().equalsIgnoreCase("Bliskie starcie")) {
            return (aktualnyGracz == 1) ? rzadBliskiGracz1 : rzadBliskiGracz2;
        } else if (karta.getPozycja().equalsIgnoreCase("Jednostki strzeleckie")) {
            return (aktualnyGracz == 1) ? rzadSrodkowyGracz1 : rzadSrodkowyGracz2;
        } else if (karta.getPozycja().equalsIgnoreCase("Oblężnicze")) {
            return (aktualnyGracz == 1) ? rzadDalszyGracz1 : rzadDalszyGracz2;
        }
        return null;
    }

    private void nastepnaTura() {
        aktualnyGracz = (aktualnyGracz == 1) ? 2 : 1;
    }

    public void wyswietlPlansze() {
        System.out.println("\nPLANSZA");

        // Sekcja Gracza 1
        System.out.println("Żetony życia Gracza 1: (" + zetonyZyciaGracz1 + "/2)");
        System.out.println("Dowódca Gracza 1: " + (dowodcaGracz1 != null ? dowodcaGracz1.getNazwa() : "Brak"));
        System.out.println("\nCmentarz Gracza 1: " + wyswietlKarty(cmentarzGracz1));

        wyswietlRzad("Machiny oblężnicze", rzadDalszyGracz1);
        wyswietlRzad("Jednostki strzeleckie", rzadSrodkowyGracz1);
        wyswietlRzad("Bliskie starcie", rzadBliskiGracz1);
        System.out.println("----------------------------------------");
        System.out.println("Karty pogodowe: " + wyswietlKarty(kartyPogodowe));
        System.out.println("----------------------------------------");

        wyswietlRzad("Bliskie starcie", rzadBliskiGracz2);
        wyswietlRzad("Jednostki strzeleckie", rzadSrodkowyGracz2);
        wyswietlRzad("Machiny oblężnicze", rzadDalszyGracz2);
        System.out.println("Cmentarz Gracza 2: " + wyswietlKarty(cmentarzGracz2));

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
        rzadBliskiGracz1.clear();
        rzadSrodkowyGracz1.clear();
        rzadDalszyGracz1.clear();
        rzadBliskiGracz2.clear();
        rzadSrodkowyGracz2.clear();
        rzadDalszyGracz2.clear();
        kartySpecjalneGracz1.clear();
        kartySpecjalneGracz2.clear();
        cmentarzGracz1.clear();
        cmentarzGracz2.clear();
        kartyPogodowe.clear();
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
}