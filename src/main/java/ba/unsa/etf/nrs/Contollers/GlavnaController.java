package ba.unsa.etf.nrs.Contollers;

import ba.unsa.etf.nrs.DataClasses.Product;
import ba.unsa.etf.nrs.PosDAO.PosDAO;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;

public class GlavnaController {

    private PosDAO dao;
    public ImageView enFlagImg;
    public ImageView bsFlagImg;
    private static boolean bosnian = false;
    private boolean choosen = false;
    public BorderPane idPane;
    public Button btnAddProduct;
    public Label lblDate;
    public Label lblTime;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private String password;
    private String username;
    public Button btnEmployees;
    public Button btnArticalReport;
    public Button btnStatusReport;
    public Button btnOrders;
    public MenuItem mItemEdit;
    public MenuItem mItemLogOut;
    public GridPane idGridProducts;

    public GlavnaController(String username, String password) {
        this.username = username;
        this.password = password;

    }

    @FXML
    public void initialize() {
        dao = dao.getInstance();
        lblDate.setText(LocalDate.now().format(formatter));
        if (dao.getUserRole(username).equals("menadzer")) {
            btnEmployees.setDisable(true);
        }
        Thread timerThread = new Thread(() -> {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
            while (true) {
                try {
                    Thread.sleep(1000); //1 second
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                final String time = simpleDateFormat.format(new Date());
                Platform.runLater(() -> {
                    lblTime.setText(time);
                });
            }
        });
        timerThread.start();

        String buttonCssProps = "-fx-pref-height:60.0; -fx-pref-width:132.0;";
        ArrayList<Product> products = dao.getProducts();
       List<Button> buttonList = new ArrayList<>();
        for (Product p : products) {
            Button button = new Button(p.getName() + ": " + p.getSellingPrice() + "KM");
            buttonList.add(button);
        }

        int k = 0;
        for(int i = 1; i < 3; i++) {
            for (int j = 1; j < 3; j++) {
                if(k >= buttonList.size()) break;
                buttonList.get(k).setStyle(buttonCssProps);
                buttonList.get(k).setWrapText(true);
                buttonList.get(k).setTextAlignment(TextAlignment.valueOf("CENTER"));
                idGridProducts.add(buttonList.get(k), j, i);
                k++;
            }
        }

    }

    private void openGlavna() {
        try {
            ResourceBundle bundle = ResourceBundle.getBundle("Translation");
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/glavna.fxml"), bundle);
            GlavnaController ctrl = new GlavnaController(username, password);
            fxmlLoader.setController(ctrl);
            Stage stage = (Stage) idPane.getScene().getWindow();
            stage.setScene(new Scene(fxmlLoader.load()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void bosanski(ActionEvent actionEvent) {
        bosnian = true;
        choosen = true;
        Locale.setDefault(new Locale("bs", "BA"));
        openGlavna();
    }

    public void engleski(ActionEvent actionEvent) {
        Locale.setDefault(new Locale("en", "US"));
        bosnian = false;
        choosen = true;
        openGlavna();
    }

    public void addProductAction(ActionEvent actionEvent) {
        try {
            ResourceBundle bundle = ResourceBundle.getBundle("Translation");
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/dodaj_artikal.fxml"), bundle);
            Parent root = fxmlLoader.load();
            Stage newStage = new Stage();
            newStage.setTitle(ResourceBundle.getBundle("Translation").getString("dodaj_artikal"));
            newStage.setScene(new Scene(root));
            newStage.setResizable(false);
            newStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addCategoryAction(ActionEvent actionEvent) {
        try {
            ResourceBundle bundle = ResourceBundle.getBundle("Translation");
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/dodaj_kategoriju.fxml"), bundle);
            Parent root = fxmlLoader.load();
            Stage newStage = new Stage();
            newStage.setTitle(ResourceBundle.getBundle("Translation").getString("Dodaj_kategoriju"));
            newStage.setScene(new Scene(root));
            newStage.setResizable(false);
            newStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void openArticalReportAction(ActionEvent actionEvent) {
        try {
            ResourceBundle bundle = ResourceBundle.getBundle("Translation");
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/izvjestajiArtikli.fxml"), bundle);
            Parent root = fxmlLoader.load();
            Stage newStage = new Stage();
            newStage.setTitle(ResourceBundle.getBundle("Translation").getString("Izvjestaj"));
            newStage.setScene(new Scene(root));
            newStage.setResizable(false);
            newStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void statusReportAction(ActionEvent actionEvent) {
        try {
            ResourceBundle bundle = ResourceBundle.getBundle("Translation");
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/izvjestajiStatus.fxml"), bundle);
            Parent root = fxmlLoader.load();
            Stage newStage = new Stage();
            newStage.setTitle(ResourceBundle.getBundle("Translation").getString("Izvjestaj"));
            newStage.setScene(new Scene(root));
            newStage.setResizable(false);
            newStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void orderReportAction(ActionEvent actionEvent) {
        try {
            ResourceBundle bundle = ResourceBundle.getBundle("Translation");
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/izvjestajiNarudzbe.fxml"), bundle);
            Parent root = fxmlLoader.load();
            Stage newStage = new Stage();
            newStage.setTitle(ResourceBundle.getBundle("Translation").getString("Izvjestaj"));
            newStage.setScene(new Scene(root));
            newStage.setResizable(false);
            newStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void logOutAction(ActionEvent actionEvent) {
        try {
            Stage stage = (Stage) idPane.getScene().getWindow();
            stage.close();
            ResourceBundle bundle = ResourceBundle.getBundle("Translation");
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"), bundle);
            Parent root = fxmlLoader.load();
            Stage newStage = new Stage();
            newStage.setTitle(ResourceBundle.getBundle("Translation").getString("title"));
            newStage.setScene(new Scene(root));
            newStage.setResizable(false);
            newStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

/*
    public void editProfileAction(ActionEvent actionEvent) {
        try {
            ResourceBundle bundle = ResourceBundle.getBundle("Translation");
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/glavna.fxml"), bundle);
            DodajKorisnikaController ctrl = new DodajKorisnikaController(dao.getUserByUsername(username));
            fxmlLoader.setController(ctrl);
            Parent root = fxmlLoader.load();
            Stage newStage = new Stage();
            newStage.setTitle(ResourceBundle.getBundle("Translation").getString("Dodaj_korisnika"));
            newStage.setScene(new Scene(root));
            newStage.setResizable(false);
            newStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

}