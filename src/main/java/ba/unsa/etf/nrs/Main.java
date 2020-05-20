package ba.unsa.etf.nrs;

import ba.unsa.etf.nrs.Contollers.GlavnaController;
import ba.unsa.etf.nrs.DataClasses.User;
import ba.unsa.etf.nrs.PosDAO.PosDAO;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.ArrayList;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        /*
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/glavna.fxml"));
        GlavnaController ctrl = new GlavnaController();
        loader.setController(ctrl);
        Parent root = loader.load();
        primaryStage.setTitle("POS Kasa");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();*/

        PosDAO dao = PosDAO.getInstance();
        User user = new User("Neko", "Nekic", "nnekic1", "password", "nnekic1@etf.unsa.ba", "061222333", "Neka adresa 167", "User-mapping.png", LocalDate.now(), "loginProvider");
        dao.addUser(user);
        ArrayList<User> users = dao.users();
        for (int i = 0; i < users.size(); i++) {
            System.out.println(users.get(i) + "\n");
        }

    }


    public static void main(String[] args) {
        launch(args);
    }
}
