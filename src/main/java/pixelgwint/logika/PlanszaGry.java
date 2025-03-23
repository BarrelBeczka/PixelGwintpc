package pixelgwint.logika;

import pixelgwint.model.Karta;
import java.util.ArrayList;
import java.util.List;

public class PlanszaGry {
    private List<Karta> rzadWreczGracz1;
    private List<Karta> rzadDystansGracz1;
    private List<Karta> rzadOblezenieGracz1;

    private List<Karta> rzadWreczGracz2;
    private List<Karta> rzadDystansGracz2;
    private List<Karta> rzadOblezenieGracz2;

    private List<Karta> kartyPogodowe;

    public PlanszaGry() {
        this.rzadWreczGracz1 = new ArrayList<>();
        this.rzadDystansGracz1 = new ArrayList<>();
        this.rzadOblezenieGracz1 = new ArrayList<>();

        this.rzadWreczGracz2 = new ArrayList<>();
        this.rzadDystansGracz2 = new ArrayList<>();
        this.rzadOblezenieGracz2 = new ArrayList<>();

        this.kartyPogodowe = new ArrayList<>();
    }

    public void dodajKarte(int gracz, Karta karta) {
        if (gracz == 1) {
            if (karta.getPozycja().equalsIgnoreCase("wręcz")) {
                rzadWreczGracz1.add(karta);
            } else if (karta.getPozycja().equalsIgnoreCase("dystans")) {
                rzadDystansGracz1.add(karta);
            } else if (karta.getPozycja().equalsIgnoreCase("oblężenie")) {
                rzadOblezenieGracz1.add(karta);
            }
        } else {
            if (karta.getPozycja().equalsIgnoreCase("wręcz")) {
                rzadWreczGracz2.add(karta);
            } else if (karta.getPozycja().equalsIgnoreCase("dystans")) {
                rzadDystansGracz2.add(karta);
            } else if (karta.getPozycja().equalsIgnoreCase("oblężenie")) {
                rzadOblezenieGracz2.add(karta);
            }
        }
    }

    public int obliczSile(int gracz) {
        int suma = 0;
        List<Karta> wrecz = (gracz == 1) ? rzadWreczGracz1 : rzadWreczGracz2;
        List<Karta> dystans = (gracz == 1) ? rzadDystansGracz1 : rzadDystansGracz2;
        List<Karta> oblezenie = (gracz == 1) ? rzadOblezenieGracz1 : rzadOblezenieGracz2;

        suma += wrecz.stream().mapToInt(Karta::getSila).sum();
        suma += dystans.stream().mapToInt(Karta::getSila).sum();
        suma += oblezenie.stream().mapToInt(Karta::getSila).sum();

        return suma;
    }

    public void wyczyscPlansze() {
        rzadWreczGracz1.clear();
        rzadDystansGracz1.clear();
        rzadOblezenieGracz1.clear();
        rzadWreczGracz2.clear();
        rzadDystansGracz2.clear();
        rzadOblezenieGracz2.clear();
        kartyPogodowe.clear();
    }
}