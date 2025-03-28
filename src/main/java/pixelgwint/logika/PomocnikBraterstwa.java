package pixelgwint.logika;

import pixelgwint.model.Karta;
import java.util.ArrayList;
import java.util.List;

public class PomocnikBraterstwa {

    public static List<Karta> znajdzKartyBraterstwa(Karta aktualnaKarta, List<Karta> wszystkieKarty) {
        List<Karta> kartyBraterstwa = new ArrayList<>();

        if (!aktualnaKarta.getUmiejetnosc().equals("Braterstwo")) {
            return kartyBraterstwa;
        }

        // Znajdź wszystkie pasujące karty (również te nie będące na ręce)
        for (Karta karta : wszystkieKarty) {
            if (karta != aktualnaKarta &&
                    aktualnaKarta.czyTworzyBraterstwo(karta) &&
                    (karta.getTyp().equals("Jednostka") || karta.getTyp().equals("Bohater"))) {
                kartyBraterstwa.add(karta);
            }
        }

        return kartyBraterstwa;
    }
}