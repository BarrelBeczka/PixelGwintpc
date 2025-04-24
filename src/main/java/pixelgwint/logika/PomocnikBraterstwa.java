package pixelgwint.logika;

import pixelgwint.model.Karta;
import java.util.ArrayList;
import java.util.List;

public class PomocnikBraterstwa {

    public static List<Karta> znajdzKartyBraterstwa(Karta aktualnaKarta, List<Karta> wszystkieKarty) {
        List<Karta> kartyBraterstwa = new ArrayList<>();

        for (Karta karta : wszystkieKarty) {
            // Dodatkowe sprawdzenie czy to nie ta sama instancja karty
            if (karta != aktualnaKarta && aktualnaKarta.czyTworzyBraterstwo(karta)) {
                kartyBraterstwa.add(karta);
            }
        }
        return kartyBraterstwa;
    }
}