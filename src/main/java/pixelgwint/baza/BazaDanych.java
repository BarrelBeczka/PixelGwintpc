package pixelgwint.baza;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import pixelgwint.model.Karta;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.nio.charset.StandardCharsets;

public class BazaDanych {
    private static final String CSV_FILE_PATH = "pixelgwintdb.csv";
    private static final char CSV_SEPARATOR = ';';

    public static List<String> pobierzTalie() {
        List<String> talie = new ArrayList<>();

        if (!new File(CSV_FILE_PATH).exists()) {
            System.err.println("BŁĄD: Brak pliku CSV w: " + new File(CSV_FILE_PATH).getAbsolutePath());
            return talie;
        }

        try (Reader reader = Files.newBufferedReader(Paths.get(CSV_FILE_PATH),StandardCharsets.UTF_8);
             CSVReader csvReader = new CSVReaderBuilder(reader)
                     .withSkipLines(1) // Pomijamy nagłówek
                     .withCSVParser(new com.opencsv.CSVParserBuilder()
                             .withSeparator(CSV_SEPARATOR)
                             .build())
                     .build()) {

            String[] record;
            while ((record = csvReader.readNext()) != null) {
                if (record.length > 6) {
                    String talia = record[6].trim();
                    if (!talia.isEmpty() && !talie.contains(talia)) {
                        talie.add(talia);
                    }
                }
            }
        } catch (IOException | CsvException e) {
            System.err.println("BŁĄD WCZYTYWANIA TALII: " + e.getMessage());
            e.printStackTrace();
        }

        return talie;
    }

    public static List<Karta> pobierzKartyZTalii(String nazwaTalii) {
        List<Karta> karty = new ArrayList<>();

        if (!new File(CSV_FILE_PATH).exists()) {
            System.err.println("BŁĄD: Brak pliku CSV!");
            return karty;
        }

        try (Reader reader = Files.newBufferedReader(Paths.get(CSV_FILE_PATH),StandardCharsets.UTF_8);
             CSVReader csvReader = new CSVReaderBuilder(reader)
                     .withSkipLines(1) // Pomijamy nagłówek
                     .withCSVParser(new com.opencsv.CSVParserBuilder()
                             .withSeparator(CSV_SEPARATOR)
                             .build())
                     .build()) {

            String[] record;
            while ((record = csvReader.readNext()) != null) {
                if (record.length >= 10 && record[6].trim().equalsIgnoreCase(nazwaTalii)) {
                    try {
                        Karta karta = new Karta(
                                parseInt(record[0]),
                                safeValue(record[1]),
                                safeValue(record[2]),
                                safeValue(record[6]),
                                parseInt(record[3]),
                                safeValue(record[5]),
                                safeValue(record[8]),
                                safeValue(record[4]),
                                safeValue(record[9]),
                                safeValue(record[7])
                        );
                        karty.add(karta);
                    } catch (Exception e) {
                        System.err.println("BŁĄD TWORZENIA KARTY: " + String.join(";", record));
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException | CsvException e) {
            System.err.println("BŁĄD WCZYTYWANIA KART: " + e.getMessage());
            e.printStackTrace();
        }

        return karty;
    }

    private static String safeValue(String value) {
        return (value == null || value.equalsIgnoreCase("N/D")) ? "Brak" : value.trim();
    }

    private static int parseInt(String value) {
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            System.err.println("NIEPRAWIDŁOWA WARTOŚĆ: " + value);
            return 0;
        }
    }
}