package ba.unsa.etf.nrs.Contollers;

import ba.unsa.etf.nrs.DAO.PosDAO;
import ba.unsa.etf.nrs.DataClasses.Role;
import ba.unsa.etf.nrs.DataClasses.User;
import ba.unsa.etf.nrs.NoInternetException;
import ba.unsa.etf.nrs.Services.AuthService;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

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
    private AuthService authService;

    public LoginController() {
        this.authService = AuthService.getInstance();
    }


    @FXML
    public void initialize() {
        dao = PosDAO.getInstance();
        btnLogin.setDefaultButton(true);
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
        Thread thread = new Thread(() -> {
        if(NoInternetException.haveInternetConnectivity()) {
            if (fldUsername.getText().isEmpty()) {
                fldUsername.getStyleClass().removeAll("ok");
                fldUsername.getStyleClass().add("notOk");
                if (!fldPassword.getText().isEmpty()) {
                    fldPassword.getStyleClass().removeAll("notOk");
                    fldPassword.getStyleClass().add("ok");
                }
            } else if (fldPassword.getText().isEmpty()) {
                fldUsername.getStyleClass().removeAll("notOk");
                fldUsername.getStyleClass().add("ok");
                fldPassword.getStyleClass().removeAll("ok");
                fldPassword.getStyleClass().add("notOk");
            } else {

                    String token = this.getTokenData();
                    if (token == null) {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                Alert a = new Alert(Alert.AlertType.NONE);
                                a.setAlertType(Alert.AlertType.WARNING);
                                if (bosnian) {
                                    a.setTitle("Upozorenje!");
                                    a.setHeaderText("Provjerite unesene podatke!");
                                    a.setContentText("Podaci nisu ispravni!");
                                } else {
                                    a.setTitle("Warning!");
                                    a.setHeaderText("Check input data!");
                                    a.setContentText("Input data are not valid!");
                                }
                                a.show();
                            }
                        });
                        fldUsername.getStyleClass().removeAll("ok");
                        fldUsername.getStyleClass().add("notOk");
                        fldPassword.getStyleClass().removeAll("ok");
                        fldPassword.getStyleClass().add("notOk");
                    } else {
                        fldUsername.getStyleClass().removeAll("notOk");
                        fldUsername.getStyleClass().add("ok");
                        fldPassword.getStyleClass().removeAll("notOk");
                        fldPassword.getStyleClass().add("ok");
                        Platform.runLater(() -> {
                            exitAction(actionEvent);
                            try {
                                if(!bosnian) Locale.setDefault(new Locale("en","US"));
                                ResourceBundle bundle = ResourceBundle.getBundle("Translation");
                                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"), bundle);
                                MainController ctrl = new MainController();
                                fxmlLoader.setController(ctrl);
                                Parent root = fxmlLoader.load();
                                Stage newStage = new Stage();
                                newStage.setTitle(ResourceBundle.getBundle("Translation").getString("title"));
                                newStage.setScene(new Scene(root));
                                newStage.setResizable(false);
                                newStage.show();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
                    }


            }
        } else {
            NoInternetException.showAlert();
        }
        });
        thread.start();
    }

    private String getTokenData() {
        String username = fldUsername.getText();
        String password = fldPassword.getText();
        String token = dao.getToken(username, password);
        if (token == null) {
            return null;
        }

        authService.setData(token, username, null);
        User user = dao.getUserByUsername(username);
        Role role = dao.getUserRole(user);
        if (role == null) return null;

        authService.setRole(role);
        return token;
    }
}
