package ba.unsa.etf.nrs;

import ba.unsa.etf.nrs.Contollers.GlavnaController;
import ba.unsa.etf.nrs.DataClasses.*;
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

        Category category = new Category(3, "slatkisi", "opis");
        Product product = new Product("Čokolada", 50, "dostupan", "mliječna čokolada", 2, 1, category);

        User user = new User("Neko", "Nekic2", "nenkic2", "1234", "nnekic2@etf.unsa.ba", "062345678", "Adresa 123", "User-mapping.png", LocalDate.now(), "loginProvider");
        PaymentType paymentType = new PaymentType("paymentTypeProvider", "description");
        Order order = new Order(user, paymentType, LocalDate.now(), "status", "orderType");

        Integer totalSum = 0;
        ArrayList<Integer> subs = dao.getSubTotals(order.getId());
        for (Integer i:subs) {
            totalSum = totalSum + i;
        }

        POS pos = new POS(order, totalSum, 1234);
        System.out.println(dao.getUser(1));
        //dao.addPos(pos);


    }


    public static void main(String[] args) {
        launch(args);
    }
}
