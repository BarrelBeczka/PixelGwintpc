package pixelgwint.model;

public class Karta {
    private int id;
    private String nazwa;
    private String typ;
    private String talia;
    private int sila;
    private String umiejetnosc;
    private String umiejetnosc_2; // Nowa kolumna
    private String pozycja;
    private String pozycja_2; // Nowa kolumna
    private String grafika;

    public Karta(int id, String nazwa, String typ, String talia, int sila, String umiejetnosc, String umiejetnosc_2, String pozycja, String pozycja_2, String grafika) {
        this.id = id;
        this.nazwa = nazwa;
        this.typ = typ;
        this.talia = talia;
        this.sila = sila;
        this.umiejetnosc = umiejetnosc;
        this.umiejetnosc_2 = umiejetnosc_2;
        this.pozycja = pozycja;
        this.pozycja_2 = pozycja_2;
        this.grafika = grafika;
    }

    public String getUmiejetnosc_2() {
        return umiejetnosc_2 != null ? umiejetnosc_2 : "Brak";
    }

    public String getPozycja_2() {
        return pozycja_2 != null ? pozycja_2 : "Brak";
    }

    public boolean maUmiejetnosc(String nazwaUmiejetnosci) {
        return nazwaUmiejetnosci.equalsIgnoreCase(umiejetnosc) ||
                (umiejetnosc_2 != null && nazwaUmiejetnosci.equalsIgnoreCase(umiejetnosc_2));
    }
    public boolean czyNalezyDoGrupy(String prefix) {
        return this.nazwa.startsWith(prefix);
    }

    public boolean czyTworzyBraterstwo(Karta innaKarta) {
        if (!this.umiejetnosc.equals("Braterstwo") || !innaKarta.umiejetnosc.equals("Braterstwo")) {
            return false;
        }

        // Specjalny przypadek - Olbrzymi krabopająk
        if (this.nazwa.equals("Olbrzymi krabopająk") && innaKarta.nazwa.equals("Krabopająk")) {
            return true;
        }

        // Grupy specjalne (Wampiry, Wiedźmy)
        if ((this.nazwa.startsWith("Wampiry: ") && innaKarta.nazwa.startsWith("Wampiry: ")) ||
                (this.nazwa.startsWith("Wiedźma: ") && innaKarta.nazwa.startsWith("Wiedźma: "))) {
            return this.sila == innaKarta.sila;
        }

        // Standardowe braterstwo
        return this.nazwa.equals(innaKarta.nazwa) &&
                this.sila == innaKarta.sila &&
                this.typ.equals("Jednostka") && // Tylko jednostki
                innaKarta.typ.equals("Jednostka");
    }

    public int getId() {
        return id;
    }

    public String getNazwa() {
        return nazwa;
    }

    public String getTyp() {
        return typ;
    }

    public String getTalia() {
        return talia;
    }

    public int getSila() {
        return sila;
    }

    public String getUmiejetnosc() {
        return umiejetnosc;
    }

    public String getPozycja() {
        return pozycja;
    }

    public String getGrafika() {
        return grafika;
    }
    public void setPozycja(String pozycja) {
        this.pozycja = pozycja;
    }
    private boolean czyZachowana = false;

    public boolean isCzyZachowana() {
        return czyZachowana;
    }

    public void setCzyZachowana(boolean czyZachowana) {
        this.czyZachowana = czyZachowana;
    }

    @Override
    public String toString() {
        return "Karta{" +
                "id=" + id +
                ", nazwa='" + nazwa + '\'' +
                ", typ='" + typ + '\'' +
                ", talia='" + talia + '\'' +
                ", sila=" + sila +
                ", umiejetnosc='" + umiejetnosc + '\'' +
                ", pozycja='" + pozycja + '\'' +
                ", grafika='" + grafika + '\'' +
                '}';
    }
    public void setSila(int sila) {
        this.sila = sila;
    }
}