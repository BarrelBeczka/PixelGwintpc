package pixelgwint.model;

import java.util.ArrayList;
import java.util.List;

public class Gracz {
    private String nazwa;
    private List<Karta> reka;
    private List<Karta> poleGry;
    private int punkty;

    public Gracz(String nazwa) {
        this.nazwa = nazwa;
        this.reka = new ArrayList<>();
        this.poleGry = new ArrayList<>();
        this.punkty = 0;
    }

    public void dodajKarteDoReki(Karta karta) {
        reka.add(karta);
    }

    public void zagrajKarte(Karta karta) {
        if (reka.contains(karta)) {
            reka.remove(karta);
            poleGry.add(karta);
            punkty += karta.getSila();
        }
    }

    public int getPunkty() {
        return punkty;
    }

    public List<Karta> getReka() {
        return reka;
    }

    public String getNazwa() {
        return nazwa;
    }
}
