package ba.unsa.etf.nrs.Contollers;

import ba.unsa.etf.nrs.DataClasses.Category;
import ba.unsa.etf.nrs.DataClasses.Product;
import ba.unsa.etf.nrs.DAO.PosDAO;
import ba.unsa.etf.nrs.Services.AuthService;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class MainController {

    private PosDAO dao;
    public ImageView enFlagImg;
    public ImageView bsFlagImg;
    private static boolean bosnian = false;
    private boolean choosen = false;
    public BorderPane idPane;
    public Button btnAddProduct;
    public Label lblDate;
    public Label lblTime;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    public Button btnEmployees;
    public Button btnArticalReport;
    public Button btnStatusReport;
    public Button btnOrders;
    public MenuItem mItemEdit;
    public MenuItem mItemLogOut;
    public GridPane idGridProducts;
    public VBox vboxCategories;
    public Label idCategoryName;
    public TextField fldSearch;
    public Button btnClearSearchText;
    public Button btnSearch;

    private AuthService authService;

    public MainController() {
        this.authService = AuthService.getInstance();
    }

    @FXML
    public void initialize() {
        dao = dao.getInstance();
        lblDate.setText(LocalDate.now().format(formatter));
        if ("menadzer".equals(this.authService.getRole())) {
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

        // dodavanje artikala iz baze za kategoriju
        String buttonCssProps = "-fx-pref-height:60.0; -fx-pref-width:132.0;";
        ArrayList<Product> products = dao.getProducts();
        showProducts(products, buttonCssProps);

        //dodavanje kategorija iz baze

        String categoryButtonCssProps = "-fx-pref-height:25.0; -fx-pref-width:158.0;";
        List<Category> categories = dao.getCategories();
        idCategoryName.setText(categories.get(0).getName());

        List<Button> categoryButtonList = new ArrayList<>();
        for (Category c : categories) {
            Button button = new Button(c.getName());
            categoryButtonList.add(button);
        }

        for(int i = 0; i < categoryButtonList.size(); i++) {
            categoryButtonList.get(i).setStyle(categoryButtonCssProps);
            categoryButtonList.get(i).setWrapText(true);
            categoryButtonList.get(i).setTextAlignment(TextAlignment.valueOf("CENTER"));
            categoryButtonList.get(i).setOnAction(getProductsForCategory(categories.get(i)));
            vboxCategories.getChildren().add(1, categoryButtonList.get(i));
        }

    }

    private EventHandler<ActionEvent> getProductsForCategory(Category category) {
        return new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                ArrayList<Product> products = dao.getProductsForCategory(category);
                String buttonCssProps = "-fx-pref-height:60.0; -fx-pref-width:132.0;";
                showProducts(products, buttonCssProps);
            }
        };
    }

    public void allProductsAction(ActionEvent actionEvent) {
        String buttonCssProps = "-fx-pref-height:60.0; -fx-pref-width:132.0;";
        ArrayList<Product> products = dao.getProducts();
        showProducts(products, buttonCssProps);
    }

    private void showProducts(ArrayList<Product> products, String buttonCssProps) {
        idGridProducts.getChildren().clear();
        List<Button> buttonList = new ArrayList<>();
        Button addButton = new Button();
        buttonList.add(addButton);
        String addBtnCss = "-fx-pref-height:50.0; -fx-pref-width:50.0; fx-background-color:lightgreen;";
        buttonList.get(0).setStyle(addBtnCss);
        buttonList.get(0).setWrapText(true);
        buttonList.get(0).setTextAlignment(TextAlignment.valueOf("CENTER"));
        buttonList.get(0).setOnAction(addProductAction());
        ImageView image = new ImageView();
        image.setImage(new Image("img/a-plus-png-5-transparent.png"));
        image.setFitHeight(50.0);
        image.setFitWidth(50.0);
        buttonList.get(0).setGraphic(image);

        for (Product p : products) {
            Button button = new Button(p.getName() + ": " + p.getSellingPrice() + "KM");
            buttonList.add(button);
        }

        int k = 0;
        for(int i = 1; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
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
            MainController ctrl = new MainController();
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

    public EventHandler addProductAction() {
        return new EventHandler() {
            @Override
            public void handle(Event event) {
                try {
                    ResourceBundle bundle = ResourceBundle.getBundle("Translation");
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/addEditProduct.fxml"), bundle);
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
        };
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

    public void openProductSalesReportAction(ActionEvent actionEvent) {
        try {
            ResourceBundle bundle = ResourceBundle.getBundle("Translation");
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/reports.fxml"), bundle);
            ProductSalesReportController ctrl = new ProductSalesReportController();
            fxmlLoader.setController(ctrl);
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
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/reports.fxml"), bundle);
            ProductsReportController ctrl = new ProductsReportController();
            fxmlLoader.setController(ctrl);
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

    public void clearSearchTextAction(ActionEvent actionEvent) {
        fldSearch.setText("");
    }
    public void btnSearchAction(ActionEvent actionEvent) {
        String buttonCssProps = "-fx-pref-height:60.0; -fx-pref-width:132.0;";
        ArrayList<Product> products = dao.getProductsByName(fldSearch.getText());
        showProducts(products, buttonCssProps);
    }

    public void btnShowEmployeesAction(ActionEvent actionEvent) {
        try {
            ResourceBundle bundle = ResourceBundle.getBundle("Translation");
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/pregled_korisnika.fxml"), bundle);
            Parent root = fxmlLoader.load();
            Stage newStage = new Stage();
            newStage.setTitle(ResourceBundle.getBundle("Translation").getString("Korisnici"));
            newStage.setScene(new Scene(root));
            newStage.setResizable(false);
            newStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void editProfileAction(ActionEvent actionEvent) {
        try {
            ResourceBundle bundle = ResourceBundle.getBundle("Translation");
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/addEditUser.fxml"), bundle);
            AddEditUserController ctrl = new AddEditUserController(dao.getUserByUsername(this.authService.getUsername()));
            fxmlLoader.setController(ctrl);
            Parent root = fxmlLoader.load();
            Stage newStage = new Stage();
            newStage.setTitle(ResourceBundle.getBundle("Translation").getString("Uredi_profil"));
            newStage.setScene(new Scene(root));
            newStage.setResizable(false);
            newStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




}
