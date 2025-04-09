package pixelgwint.baza;

import pixelgwint.model.Karta;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BazaDanych {
    private static final String URL = "jdbc:mysql://34.116.199.192:3306/pixelgwint";
    private static final String USER = "pixelgwint_u1";
    private static final String PASSWORD = "5636d5ca";


    public static Connection polacz() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            System.out.println("Błąd podczas łączenia z bazą danych: " + e.getMessage());
            return null;
        }
    }

    public static void rozlacz(Connection connection) {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            System.out.println("Błąd podczas rozłączania z bazą danych: " + e.getMessage());
        }
    }

    public static List<String> pobierzTalie() {
        List<String> talie = new ArrayList<>();
        String sql = "SELECT DISTINCT talia FROM Karty";

        try (Connection connection = polacz();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                talie.add(resultSet.getString("talia"));
            }

        } catch (SQLException e) {
            System.out.println("Błąd podczas pobierania talii: " + e.getMessage());
        }

        return talie;
    }

    public static List<Karta> pobierzKartyZTalii(String nazwaTalii) {
        List<Karta> karty = new ArrayList<>();
        String sql = "SELECT * FROM Karty WHERE talia = ?";

        try (Connection connection = polacz();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, nazwaTalii);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Karta karta = new Karta(
                        resultSet.getInt("id"),
                        resultSet.getString("nazwa").trim(), // Dodaj trim() przy wczytywaniu nazwy
                        resultSet.getString("typ"),
                        resultSet.getString("talia"),
                        resultSet.getInt("punkty_sily"),
                        resultSet.getString("umiejetnosc"),
                        resultSet.getString("umiejetnosc_2"), // Nowa kolumna
                        resultSet.getString("pozycja"),
                        resultSet.getString("pozycja_2"), // Nowa kolumna
                        resultSet.getString("grafika")
                );
                karty.add(karta);
            }
        } catch (SQLException e) {
            System.out.println("Błąd podczas pobierania kart z talii: " + e.getMessage());
        }

        return karty;
    }
}
