package ba.unsa.etf.nrs.Contollers;

import ba.unsa.etf.nrs.DataClasses.Role;
import ba.unsa.etf.nrs.DataClasses.User;
import ba.unsa.etf.nrs.PosDAO.PosDAO;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import jdk.nashorn.internal.parser.JSONParser;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class AddEditUserController {

    public Button btnBack;
    public Button btnSave;
    public Button btnDelete;
    public Button btnChangePicture;

    public TextField fldFirstName;
    public TextField fldLastName;
    public TextField fldAddress;
    public TextField fldEmail;
    public TextField  fldPhone;
    public TextField fldUsername;
    public TextField fldPassword;
    public ChoiceBox<Role> choiceBoxRole;
    public DatePicker dpBirthDate;

    public GridPane idPane;

    private PosDAO dao;
    private User user = null;


    public AddEditUserController(User user) {
        this.user = user;
    }

    public AddEditUserController() {
    }

    @FXML
    public void initialize() {
        dao = PosDAO.getInstance();
        choiceBoxRole.setItems(FXCollections.observableArrayList(dao.getRoles()));
        if(user != null) {
            fldFirstName.setText(user.getFirstName());
            fldLastName.setText(user.getLastName());
            fldAddress.setText(user.getAddress());
            fldEmail.setText(user.getEmail());
            fldPhone.setText(user.getPhone());
            fldUsername.setText(user.getUsername());
            fldPassword.setText(user.getPassword());
            fldPassword.setDisable(true);
            choiceBoxRole.setValue(dao.getUserRole(user));
            dpBirthDate.setValue(user.getBirthDate());
        }
    }

    public void backAction(ActionEvent actionEvent) {
        Stage stage = (Stage) idPane.getScene().getWindow();
        stage.close();
    }

    public void saveAction(ActionEvent actionEvent) {
        boolean sveOk = true;
        sveOk = isEmpty(fldFirstName);
        sveOk &= isEmpty(fldLastName);
        sveOk &= isEmpty(fldAddress);
        sveOk &= isEmpty(fldEmail);
        sveOk &= isEmpty(fldUsername);

        if (!sveOk){
            user = null;
            return;
        }


        new Thread(() -> {
            try {
                URL location = new URL("https://api.email-validator.net/api/verify?EmailAddress=" + fldEmail.getText()+ "&APIKey=ev-8e447efe7fc80859c2328a1b795475bc");
                fldEmail.getStyleClass().removeAll("ok");
                fldEmail.getStyleClass().removeAll("notOk");
                fldEmail.getStyleClass().add("checking");
                String json = "";
                String line = null;
                BufferedReader entry = null;
                try {
                    entry = new BufferedReader(new InputStreamReader(location.openStream(), StandardCharsets.UTF_8));
                    while ((line = entry.readLine()) != null)
                        json = json + line;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                JSONObject jo = new JSONObject(json);
                System.out.println(jo.getInt("status") + "       Amraaa");
                if (jo.getInt("status") == 302) {
                    Platform.runLater(() -> {
                        fldEmail.getStyleClass().removeAll("checking");
                        fldEmail.getStyleClass().add("notOk");
                    });
                } else {
                    Platform.runLater(() -> {
                        fldEmail.getStyleClass().removeAll("checking");
                        fldEmail.getStyleClass().add("ok");
                        Stage stage = (Stage) fldEmail.getScene().getWindow();
                        stage.close();
                    });
                }
            } catch (Exception ex) {
            }
        }).start();

        if (user == null) {
            user = new User();
            user.setFirstName(fldFirstName.getText());
            user.setLastName(fldLastName.getText());
            user.setAddress(fldAddress.getText());
            user.setEmail(fldEmail.getText());
            user.setPhone(fldPhone.getText());
            user.setUsername(fldUsername.getText());
            user.setPassword(fldPassword.getText());
            user.setBirthDate(dpBirthDate.getValue());
            int userId = dao.addUser(user);
            user.setId(userId);
            Role selectedRole = choiceBoxRole.getValue();
            dao.addUserRole(user, selectedRole);
        }
    }

    private boolean isEmpty(TextField field) {
        if (field.getText().trim().isEmpty()) {
            field.getStyleClass().removeAll("ok");
            field.getStyleClass().add("notOk");
            return false;
        } else {
            field.getStyleClass().removeAll("notOk");
            field.getStyleClass().add("ok");
        }
        return true;
    }
    public void deleteAction(ActionEvent actionEvent) {
        Stage stage = (Stage) idPane.getScene().getWindow();
        stage.close();
    }
}
