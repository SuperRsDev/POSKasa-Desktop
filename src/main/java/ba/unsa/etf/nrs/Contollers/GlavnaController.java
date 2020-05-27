package ba.unsa.etf.nrs.Contollers;

import ba.unsa.etf.nrs.PosDAO.PosDAO;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;

public class GlavnaController {

    private PosDAO dao;
    public ImageView enFlagImg;
    public ImageView bsFlagImg;
    private static boolean bosnian = false;
    private boolean choosen = false;
    public BorderPane idPane;


    @FXML
    public void initialize() {
        dao = dao.getInstance();


    }

    private void openLogin() {
        try {
            ResourceBundle bundle = ResourceBundle.getBundle("Translation");
            Stage stage = (Stage) idPane.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader( getClass().getResource("/fxml/glavna.fxml" ), bundle);
            stage.setScene(new Scene(loader.load()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void bosanski(ActionEvent actionEvent) {
        bosnian = true;
        choosen = true;
        Locale.setDefault(new Locale("bs", "BA"));
        openLogin();
    }

    public void engleski(ActionEvent actionEvent) {
        Locale.setDefault(new Locale("en", "US"));
        bosnian = false;
        choosen = true;
        openLogin();
    }
}