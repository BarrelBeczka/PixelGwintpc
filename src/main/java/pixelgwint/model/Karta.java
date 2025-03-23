package pixelgwint.model;

public class Karta {
    private int id;
    private String nazwa;
    private String typ;
    private String talia;
    private int sila;
    private String umiejetnosc;
    private String pozycja;
    private String grafika;

    public Karta(int id, String nazwa, String typ, String talia, int sila, String umiejetnosc, String pozycja, String grafika) {
        this.id = id;
        this.nazwa = nazwa;
        this.typ = typ;
        this.talia = talia;
        this.sila = sila;
        this.umiejetnosc = umiejetnosc;
        this.pozycja = pozycja;
        this.grafika = grafika;
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
}