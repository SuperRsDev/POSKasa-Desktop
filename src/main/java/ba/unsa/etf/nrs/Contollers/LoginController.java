package ba.unsa.etf.nrs.Contollers;

import ba.unsa.etf.nrs.PosDAO.PosDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import javax.swing.text.PasswordView;
import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

public class LoginController {

    private  PosDAO dao;
    public Button btnDeleteUsername;
    public Button btnDeletePassword;
    public Button btnLogin;
    public Button btnExit;
    public TextField fldUsername;
    public PasswordField fldPassword;
    public ImageView enFlagImg;
    public ImageView bsFlagImg;
    public GridPane gridId;
    private static boolean bosnian = false;
    private boolean choosen = false;




    @FXML
    public void initialize() {
        //dao = PosDAO.getInstance();

        bsFlagImg.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            bosnian = true;
            choosen = true;
            Locale.setDefault(new Locale("bs", "BA"));
            openLogin();
        });

        enFlagImg.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            Locale.setDefault(new Locale("en", "US"));
            bosnian = false;
            choosen = true;
            openLogin();
        });

        fldUsername.textProperty().addListener(((observableValue, s, username) -> {
            if (username.length() >= 4 && username.length() <= 15) {
                fldUsername.getStyleClass().removeAll("notOk");
                fldUsername.getStyleClass().add("ok");
            } else {
                fldUsername.getStyleClass().removeAll("ok");
                fldUsername.getStyleClass().add("notOk");
            }
        }));

        fldPassword.textProperty().addListener(((observableValue, s, password) -> {
            if (password.length() >= 3 && password.length() <= 15) {
                fldPassword.getStyleClass().removeAll("notOk");
                fldPassword.getStyleClass().add("ok");
            } else {
                fldPassword.getStyleClass().removeAll("ok");
                fldPassword.getStyleClass().add("notOk");
            }
        }));
    }

    private void openLogin() {
        try {
            ResourceBundle bundle = ResourceBundle.getBundle("Translation");
            Stage stage = (Stage) gridId.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader( getClass().getResource("/fxml/login.fxml" ), bundle);
            stage.setScene(new Scene(loader.load()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void exitAction(ActionEvent actionEvent) {
        Stage stage = (Stage) btnExit.getScene().getWindow();
        stage.close();
    }

    public void deleteUsernameTextAction(ActionEvent actionEvent) {
        fldUsername.setText("");
    }

    public void deletePasswordTextAction(ActionEvent actionEvent) {
        fldPassword.setText("");
    }

    public void loginAction(ActionEvent actionEvent) {

    }
}
