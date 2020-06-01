package ba.unsa.etf.nrs;

import ba.unsa.etf.nrs.Contollers.GlavnaController;
import ba.unsa.etf.nrs.Contollers.LoginController;
import ba.unsa.etf.nrs.DataClasses.*;
import ba.unsa.etf.nrs.PosDAO.PosDAO;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.ResourceBundle;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        //Todo: bundle prevod da je uvijek isti kao izabrani - provjeriti
        ResourceBundle bundle = ResourceBundle.getBundle("Translation");
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"), bundle);
        primaryStage.setTitle("POS");
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);
        primaryStage.show();
/*        PosDAO dao = PosDAO.getInstance();

        Category category = new Category( "slatkisi", "opis");
        dao.addCategory(category);

        Product product = new Product("Čokolada", 50, "dostupan", "mliječna čokolada", 2, 1, dao.getCategory(3));
        dao.addProduct(product);

        User user = new User("Neko", "Nekic2", "nenkic2", "1234", "nnekic2@etf.unsa.ba", "062345678", "Adresa 123", "User-mapping.png", LocalDate.now(), "loginProvider");
        dao.addUser(user);

        Order order = new Order(dao.getUser(1), dao.getPaymentType(1), LocalDate.now(), "status", "orderType");
        dao.addOrder(order);

        Integer totalSum = 0;
        ArrayList<Integer> subs = dao.getSubTotals(1);
        for (Integer i:subs) {
            totalSum = totalSum + i;
        }

        POS pos = new POS(dao.getOrder(3), totalSum, 1234);
        dao.addPos(pos);

        //dao.updateProduct(2, "Čokolada", 49, "dostupan", "mliječna čokolada", 2, 1, dao.getCategoryByName("Hrana"));
        System.out.println(dao.loginValid("apozegija1", "amraamra"));
*/
    }


    public static void main(String[] args) {
        launch(args);
    }
}
