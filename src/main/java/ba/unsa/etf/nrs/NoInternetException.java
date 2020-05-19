package ba.unsa.etf.nrs;

import javafx.scene.control.Alert;

import java.net.URL;
import java.net.URLConnection;

public class NoInternetException {
    public static void showAlert() {
        String warning = "Upozorenje";
        String headerNoInternet = "Problem sa konekcijom";
        String noInternet = "Uređaj nije povezan sa internetom.";
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(warning);
        alert.setHeaderText(headerNoInternet);
        alert.setContentText(noInternet);
        alert.showAndWait();
    }

    public static boolean haveInternetConnectivity() {
        boolean haveInternet = false;
        try {
            URL url = new URL("https://www.google.com/");
            URLConnection connection = url.openConnection();
            connection.connect();

            System.out.println("Konekcija uspješna");
            haveInternet = true;
        }
        catch (Exception e) {
            System.out.println("Nema internet konekcije");
        }
        return haveInternet;
    }

}