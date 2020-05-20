package ba.unsa.etf.nrs;

import ba.unsa.etf.nrs.Contollers.GlavnaController;
import ba.unsa.etf.nrs.DataClasses.Category;
import ba.unsa.etf.nrs.DataClasses.Product;
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
        Category category = new Category(3, "slatkisi", "opis");
        Product product = new Product("Čokolada", 50, "dostupan", "mliječna čokolada", 2, 1, category);

        PosDAO dao = PosDAO.getInstance();
        dao.addProduct(product);
        ArrayList<Category> categories = dao.categories();
        for (int i = 0; i < categories.size(); i++) {
            System.out.println(categories.get(i));
        }

        System.out.println(" ... ");

        ArrayList<Product> products = dao.products();
        for (int i = 0; i < products.size(); i++) {
            System.out.println(products.get(i));
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}
