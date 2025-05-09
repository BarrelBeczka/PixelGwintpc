package pixelgwint;

import java.util.*;
import pixelgwint.baza.BazaDanych;
import pixelgwint.logika.Gra;
import pixelgwint.model.Karta;
import pixelgwint.logika.PomocnikBraterstwa;

public class Main {
    private static String przypiszUmiejetnoscTalii(String nazwaTalii) {
        if (nazwaTalii == null || nazwaTalii.isEmpty()) return "";

        String normalized = nazwaTalii.trim().toLowerCase();
        if (normalized.contains("północy") || normalized.contains("polnocy")) {
            return "Królestwo Północy";
        }
        else if (normalized.contains("nilfgaard") || normalized.contains("nilfgard")) {
            return "Nilfgaard";
        }
        else if (normalized.contains("scoia'tael") || normalized.contains("scoia tael") ||
                 normalized.contains("scoiatael")) {
            return "Scoia'tael";
        }
        return "";
    }
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        List<String> talie = BazaDanych.pobierzTalie();
        if (talie.isEmpty()) {
            System.out.println("Brak dostępnych talii w bazie!");
            return;
        }

        System.out.println("Dostępne talie:");
        for (int i = 0; i < talie.size(); i++) {
            System.out.println((i + 1) + ". " + talie.get(i));
        }

        System.out.print("Gracz 1, wybierz talię (podaj numer): ");
        int wybor1 = scanner.nextInt();
        String taliaGracza1 = talie.get(wybor1 - 1);
        scanner.nextLine(); // Czyść bufor

        System.out.print("Gracz 2, wybierz talię (podaj numer): ");
        int wybor2 = scanner.nextInt();
        String taliaGracza2 = talie.get(wybor2 - 1);
        scanner.nextLine(); // Czyść bufor

        int pierwszyGracz; // TYLKO JEDNA DEKLARACJA - NIE ZMIENIAJ TEGO
        String umiejetnosc1 = przypiszUmiejetnoscTalii(taliaGracza1);
        String umiejetnosc2 = przypiszUmiejetnoscTalii(taliaGracza2);

        if ("Scoia'tael".equals(umiejetnosc1) && !"Scoia'tael".equals(umiejetnosc2)) {
            System.out.println("\nGracz 1 wybrał Scoia'tael - wybiera kto zaczyna:");
            System.out.println("1. Ja (Gracz 1)");
            System.out.println("2. Przeciwnik (Gracz 2)");
            int wybor = scanner.nextInt();
            pierwszyGracz = (wybor == 1) ? 0 : 1;
            scanner.nextLine(); // Czyść bufor
        }
        else if ("Scoia'tael".equals(umiejetnosc2) && !"Scoia'tael".equals(umiejetnosc1)) {
            System.out.println("\nGracz 2 wybrał Scoia'tael - wybiera kto zaczyna:");
            System.out.println("1. Ja (Gracz 2)");
            System.out.println("2. Przeciwnik (Gracz 1)");
            int wybor = scanner.nextInt();
            pierwszyGracz = (wybor == 1) ? 1 : 0;
            scanner.nextLine(); // Czyść bufor
        }
        else {
            pierwszyGracz = rzutMoneta();
        }

        System.out.println("Gracz 1 wybiera karty:");
        List<Karta> talia1 = wybierzKarty(BazaDanych.pobierzKartyZTalii(taliaGracza1));
        dowodcaGracza1 = znajdzDowodce(talia1);

        System.out.println("Gracz 2 wybiera karty:");
        List<Karta> talia2 = wybierzKarty(BazaDanych.pobierzKartyZTalii(taliaGracza2));
        dowodcaGracza2 = znajdzDowodce(talia2);  // Znajdź dowódcę w talii Gracza 2
        boolean gracz1Stokrotka = dowodcaGracza1 != null && dowodcaGracza1.getNazwa().contains("Stokrotka z Dolin");
        boolean gracz2Stokrotka = dowodcaGracza2 != null && dowodcaGracza2.getNazwa().contains("Stokrotka z Dolin");

        List<Karta> rekaGracza1 = wylosujKartyDoGry(talia1, 10, gracz1Stokrotka);
        List<Karta> rekaGracza2 = wylosujKartyDoGry(talia2, 10, gracz2Stokrotka);


        if (pierwszyGracz == 0) {
            System.out.println("\nGracz 1 zaczyna! Możesz wymienić 2 karty.");
            rekaGracza1 = wymienKarty(rekaGracza1, talia1, scanner);
        } else {
            System.out.println("\nGracz 2 zaczyna! Możesz wymienić 2 karty.");
            rekaGracza2 = wymienKarty(rekaGracza2, talia2, scanner);
        }

        // Wymiana kart dla drugiego gracza
        if (pierwszyGracz == 0) {
            System.out.println("\nTeraz Gracz 2 może wymienić 2 karty.");
            rekaGracza2 = wymienKarty(rekaGracza2, talia2, scanner);
        } else {
            System.out.println("\nTeraz Gracz 1 może wymienić 2 karty.");
            rekaGracza1 = wymienKarty(rekaGracza1, talia1, scanner);
        }

        Gra gra = new Gra(rekaGracza1, rekaGracza2, pierwszyGracz,
                dowodcaGracza1, dowodcaGracza2,
                talia1, talia2,
                taliaGracza1, taliaGracza2);
        gra.rozpocznijGre();
    }
    public static int rzutMoneta() {
        Random random = new Random();
        int wynik = random.nextInt(2); // 0 lub 1
        if (wynik == 0) {
            System.out.println("Rzut monetą: Gracz 1 zaczyna!");
        } else {
            System.out.println("Rzut monetą: Gracz 2 zaczyna!");
        }
        return wynik;
    }

    public static List<Karta> wybierzKarty(List<Karta> wszystkieKarty) {
        Scanner scanner = new Scanner(System.in);
        List<Karta> wybraneKarty = new ArrayList<>();
        Set<Integer> wybraneIndeksy = new HashSet<>();
        Map<Karta, Integer> oryginalneNumery = new HashMap<>();
        boolean dowodcaWybrany = false;
        Karta aktualnyDowodca = null;

        while (true) {
            wyswietlKarty(wszystkieKarty, wybraneIndeksy, dowodcaWybrany);
            System.out.println("Wybierz karty: (aby zakończyć wpisz 'Koniec', aby zobaczyć swoją talię wpisz 'Talia')");

            String linia = scanner.nextLine();

            if (linia.equalsIgnoreCase("Koniec")) {
                if (!dowodcaWybrany) {
                    System.out.println("Błąd: Musisz wybrać dowódcę!");
                    continue;
                }
                if (!sprawdzLiczbeKartJednostek(wybraneKarty)) {
                    continue;
                }
                if (!sprawdzLiczbeKartSpecjalnych(wybraneKarty)) {
                    continue;
                }
                break;
            }
            if (linia.equalsIgnoreCase("Talia")) {
                wyswietlTalie(wybraneKarty, oryginalneNumery);
                dowodcaWybrany = usunKarteZTalii(wybraneKarty, wybraneIndeksy, wszystkieKarty, scanner, oryginalneNumery, dowodcaWybrany);
                if (!dowodcaWybrany) {
                    aktualnyDowodca = null; // Resetujemy dowódcę
                }
                continue;
            }

            String[] wybory = linia.split(" ");
            for (String wybor : wybory) {
                try {
                    int numer = Integer.parseInt(wybor) - 1;
                    if (numer >= 0 && numer < wszystkieKarty.size() && !wybraneIndeksy.contains(numer)) {
                        Karta karta = wszystkieKarty.get(numer);

                        if (karta.getTyp().equalsIgnoreCase("Dowódca")) {
                            if (dowodcaWybrany) {
                                System.out.println("Dowódcę wybiera się tylko raz!");
                                continue;
                            }
                            dowodcaWybrany = true;
                            aktualnyDowodca = karta;
                        }

                        wybraneKarty.add(karta);
                        wybraneIndeksy.add(numer);
                        oryginalneNumery.put(karta, numer + 1);
                    }
                } catch (NumberFormatException ignored) {}
            }
        }
        return wybraneKarty;
    }

    public static void wyswietlKarty(List<Karta> karty, Set<Integer> wybraneIndeksy, boolean dowodcaWybrany) {
        System.out.println("\nDowódca:");
        for (int i = 0; i < karty.size(); i++) {
            Karta karta = karty.get(i);
            if (!wybraneIndeksy.contains(i) && karta.getTyp().equalsIgnoreCase("Dowódca")) {
                if (dowodcaWybrany) continue; // Ukryj inne karty dowódców
                wyswietlKarte(i, karta);
            }
        }

        System.out.println("\nJednostki:");
        for (int i = 0; i < karty.size(); i++) {
            Karta karta = karty.get(i);
            if (!wybraneIndeksy.contains(i) && karta.getTyp().equalsIgnoreCase("Jednostka")) {
                wyswietlKarte(i, karta);
            }
        }

        System.out.println("\nBohaterowie:");
        for (int i = 0; i < karty.size(); i++) {
            Karta karta = karty.get(i);
            if (!wybraneIndeksy.contains(i) && karta.getTyp().equalsIgnoreCase("Bohater")) {
                wyswietlKarte(i, karta);
            }
        }

        System.out.println("\nSpecjalne:");
        for (int i = 0; i < karty.size(); i++) {
            Karta karta = karty.get(i);
            if (!wybraneIndeksy.contains(i) && karta.getTyp().equalsIgnoreCase("Specjalna")) {
                wyswietlKarte(i, karta);
            }
        }
    }

    private static void wyswietlKarte(int indeks, Karta karta) {
        String umiejetnosc = karta.getUmiejetnosc().equalsIgnoreCase("Brak") ? "" : " (Umiejętność: " + karta.getUmiejetnosc();
        if (!karta.getUmiejetnosc_2().equalsIgnoreCase("Brak")) {
            umiejetnosc += ", " + karta.getUmiejetnosc_2();
        }
        umiejetnosc += ")";

        String pozycja = karta.getPozycja();
        if (!karta.getPozycja_2().equalsIgnoreCase("Brak")) {
            pozycja += "/" + karta.getPozycja_2();
        }

        System.out.println((indeks + 1) + ". " + karta.getNazwa() +
                " | Siła: " + karta.getSila() +
                " | Pozycja: " + pozycja +
                umiejetnosc);
    }

    public static void wyswietlTalie(List<Karta> talia, Map<Karta, Integer> oryginalneNumery) {
        System.out.println("\nTwoja talia:");

        // Liczenie kart
        long liczbaDowodcow = talia.stream().filter(k -> k.getTyp().equalsIgnoreCase("Dowódca")).count();
        long liczbaJednostek = talia.stream().filter(k -> k.getTyp().equalsIgnoreCase("Jednostka") || k.getTyp().equalsIgnoreCase("Bohater")).count();
        long liczbaSpecjalnych = talia.stream().filter(k -> k.getTyp().equalsIgnoreCase("Specjalna")).count();

        // Wyświetlenie podsumowania
        System.out.println("Dowódca (" + liczbaDowodcow + "/1) Jednostki (" + liczbaJednostek + "/22+) Specjalne (" + liczbaSpecjalnych + "/10)\n");

        // Podział kart na typy
        Map<String, List<Karta>> podzial = new LinkedHashMap<>();
        podzial.put("Dowódca:", new ArrayList<>());
        podzial.put("Jednostki:", new ArrayList<>());
        podzial.put("Bohaterowie:", new ArrayList<>());
        podzial.put("Specjalne:", new ArrayList<>());

        for (Karta karta : talia) {
            if (karta.getTyp().equalsIgnoreCase("Dowódca")) {
                podzial.get("Dowódca:").add(karta);
            } else if (karta.getTyp().equalsIgnoreCase("Jednostka")) {
                podzial.get("Jednostki:").add(karta);
            } else if (karta.getTyp().equalsIgnoreCase("Bohater")) {
                podzial.get("Bohaterowie:").add(karta);
            } else {
                podzial.get("Specjalne:").add(karta);
            }
        }

        // Wyświetlanie kart w talii z oryginalnym numerem
        for (Map.Entry<String, List<Karta>> entry : podzial.entrySet()) {
            if (!entry.getValue().isEmpty()) {
                System.out.println(entry.getKey());
                for (Karta karta : entry.getValue()) {
                    int oryginalnyNumer = oryginalneNumery.get(karta);
                    String umiejetnosc = karta.getUmiejetnosc().equalsIgnoreCase("Brak") ? "" : " (Umiejętność: " + karta.getUmiejetnosc() + ")";
                    System.out.println(oryginalnyNumer + ". " + karta.getNazwa() + " | Siła: " + karta.getSila() + " | Pozycja: " + karta.getPozycja() + umiejetnosc);
                }
            }
        }
        System.out.println();
    }

    public static boolean sprawdzLiczbeKartSpecjalnych(List<Karta> talia) {
        long liczbaSpecjalnych = talia.stream()
                .filter(k -> k.getTyp().equalsIgnoreCase("Specjalna"))
                .count();

        if (liczbaSpecjalnych > 10) {
            System.out.println("Błąd: Wybrano za dużo kart specjalnych! Limit to 10.");
            return false;
        }
        return true;
    }

    public static boolean sprawdzLiczbeKartJednostek(List<Karta> talia) {
        long liczbaJednostek = talia.stream()
                .filter(k -> k.getTyp().equalsIgnoreCase("Jednostka") || k.getTyp().equalsIgnoreCase("Bohater"))
                .count();

        if (liczbaJednostek < 22) {
            System.out.println("Błąd: Musisz wybrać co najmniej 22 jednostki! Brakuje: " + (22 - liczbaJednostek));
            return false;
        }
        return true;
    }

    public static boolean usunKarteZTalii(List<Karta> talia, Set<Integer> wybraneIndeksy, List<Karta> wszystkieKarty, Scanner scanner, Map<Karta, Integer> oryginalneNumery, boolean dowodcaWybrany) {
        boolean dowodcaUsuniety = false;

        while (true) {
            System.out.println("Wpisz 'Usuń [numer]' aby usunąć kartę lub 'Powrót' aby wrócić do wyboru kart.");
            String input = scanner.nextLine();

            if (input.equalsIgnoreCase("Powrót")) {
                break;
            }

            if (input.startsWith("Usuń")) {
                String[] parts = input.split(" ");
                if (parts.length == 2) {
                    try {
                        int numerDoUsuniecia = Integer.parseInt(parts[1]); // Pobieramy numer użytkownika
                        Karta kartaDoUsuniecia = null;

                        // Znajdujemy kartę po oryginalnym numerze
                        for (Map.Entry<Karta, Integer> entry : oryginalneNumery.entrySet()) {
                            if (entry.getValue() == numerDoUsuniecia) {
                                kartaDoUsuniecia = entry.getKey();
                                break;
                            }
                        }

                        if (kartaDoUsuniecia == null || !talia.contains(kartaDoUsuniecia)) {
                            System.out.println("Błąd: Nie znaleziono karty o podanym numerze.");
                            continue;
                        }

                        // Usunięcie karty z talii
                        talia.remove(kartaDoUsuniecia);
                        wybraneIndeksy.remove(numerDoUsuniecia - 1);
                        oryginalneNumery.remove(kartaDoUsuniecia);
                        wszystkieKarty.add(kartaDoUsuniecia); // Przywracamy kartę do dostępnych

                        System.out.println("Usunięto kartę: " + kartaDoUsuniecia.getNazwa());

                        // Jeśli usunięto dowódcę, resetujemy flagę
                        if (kartaDoUsuniecia.getTyp().equalsIgnoreCase("Dowódca")) {
                            dowodcaUsuniety = true;
                        }

                    } catch (NumberFormatException e) {
                        System.out.println("Błąd: Podaj poprawny numer karty.");
                    }
                } else {
                    System.out.println("Błąd: Wpisz 'Usuń [numer]' aby usunąć kartę.");
                }
            }
        }
        return !dowodcaUsuniety; // Jeśli dowódca został usunięty, zwracamy false
    }
    public static List<Karta> wylosujKartyDoGry(List<Karta> talia, int liczbaKart, boolean czyStokrotkaZDolin) {
        List<Karta> taliaBezDowodcy = new ArrayList<>();

        // Filtrowanie talii - usuwamy karty Dowódcy
        for (Karta karta : talia) {
            if (!karta.getTyp().equalsIgnoreCase("Dowódca")) {
                taliaBezDowodcy.add(karta);
            }
        }

        // Jeśli to Stokrotka z Dolin, zwiększ liczbę kart o 1
        int rzeczywistaLiczbaKart = czyStokrotkaZDolin ? liczbaKart + 1 : liczbaKart;

        // Tasowanie kart i losowanie ręki
        Collections.shuffle(taliaBezDowodcy);
        return taliaBezDowodcy.subList(0, Math.min(rzeczywistaLiczbaKart, taliaBezDowodcy.size()));
    }    private static Karta dowodcaGracza1 = null;
    private static Karta dowodcaGracza2 = null;

    private static Karta znajdzDowodce(List<Karta> talia) {
        for (Karta karta : talia) {
            if (karta.getTyp().equalsIgnoreCase("Dowódca")) {
                return karta;
            }
        }
        return null;
    }
    public static List<Karta> wymienKarty(List<Karta> rekaGracza, List<Karta> talia, Scanner scanner) {
        System.out.println("\nOto twoja wylosowana ręka:");
        wyswietlReke(rekaGracza);

        // Tworzymy kopię talii bez dowódców
        List<Karta> taliaBezDowodcy = new ArrayList<>();
        for (Karta k : talia) {
            if (!k.getTyp().equalsIgnoreCase("Dowódca")) {
                taliaBezDowodcy.add(k);
            }
        }

        int liczbaZmian = 0;
        while (liczbaZmian < 2 && !taliaBezDowodcy.isEmpty()) {
            System.out.println("\nMożesz wymienić jeszcze " + (2 - liczbaZmian) + " karty.");
            System.out.print("Wybierz kartę do zmiany (1-" + rekaGracza.size() + ") lub wpisz 'Gotowe': ");
            String wybor = scanner.nextLine();

            if (wybor.equalsIgnoreCase("Gotowe")) {
                break;
            }

            try {
                int numerKarty = Integer.parseInt(wybor) - 1;
                if (numerKarty >= 0 && numerKarty < rekaGracza.size()) {
                    // Losujemy nową kartę
                    Random random = new Random();
                    int indexNowejKarty = random.nextInt(taliaBezDowodcy.size());
                    Karta nowaKarta = taliaBezDowodcy.remove(indexNowejKarty);

                    // Wymieniamy karty
                    Karta usunietaKarta = rekaGracza.remove(numerKarty);
                    rekaGracza.add(nowaKarta);
                    talia.add(usunietaKarta); // Oddajemy starą kartę do talii

                    System.out.println("Wymieniono kartę: " + usunietaKarta.getNazwa() +
                            " na: " + nowaKarta.getNazwa());
                    liczbaZmian++;

                    System.out.println("\nTwoja nowa ręka:");
                    wyswietlReke(rekaGracza);
                } else {
                    System.out.println("Niepoprawny numer karty!");
                }
            } catch (NumberFormatException e) {
                System.out.println("Wpisz numer karty lub 'Gotowe'.");
            }
        }
        return rekaGracza;
    }
    public static void wyswietlReke(List<Karta> reka) {
        for (int i = 0; i < reka.size(); i++) {
            Karta karta = reka.get(i);
            String umiejetnosc = karta.getUmiejetnosc().equalsIgnoreCase("Brak") ? "" : " (Umiejętność: " + karta.getUmiejetnosc() + ")";
            System.out.println((i + 1) + ". " + karta.getNazwa() + " | Siła: " + karta.getSila() + " | Pozycja: " + karta.getPozycja() + umiejetnosc);
        }
    }
}