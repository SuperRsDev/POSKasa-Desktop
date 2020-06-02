package ba.unsa.etf.nrs.Contollers;

import ba.unsa.etf.nrs.DAO.EmailDao;
import ba.unsa.etf.nrs.DataClasses.Role;
import ba.unsa.etf.nrs.DataClasses.User;
import ba.unsa.etf.nrs.DAO.PosDAO;
import ba.unsa.etf.nrs.Services.AuthService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import jdk.nashorn.internal.runtime.regexp.joni.Regex;
import org.json.JSONObject;

import javax.swing.*;
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
    public PasswordField fldPassword;
    public ChoiceBox<Role> choiceBoxRole;
    public DatePicker dpBirthDate;

    public GridPane idPaneAddUser;

    private PosDAO dao;
    private EmailDao emailDao;
    private User user = null;
    private AuthService authService;


    public AddEditUserController(User user) {
        this.user = user;
        dao = PosDAO.getInstance();
        emailDao = EmailDao.getInstance();
        this.authService = AuthService.getInstance();
    }

    public AddEditUserController() {
        dao = PosDAO.getInstance();
        emailDao = EmailDao.getInstance();
        this.authService = AuthService.getInstance();
    }

    @FXML
    public void initialize() {
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
            System.out.println(dao.getUserRole(user).getName());
            Role role = dao.getUserRole(user);
            if(role != null) {
                Role selectedRole = role;
                for (Role choiceRole : choiceBoxRole.getItems()) {
                    if (choiceRole.getId() == role.getId()) {
                        selectedRole = choiceRole;
                    }
                }

                choiceBoxRole.getSelectionModel().select(selectedRole);
            } else {
                choiceBoxRole.getSelectionModel().selectFirst();
            }
            dpBirthDate.setValue(user.getBirthDate());
        } else choiceBoxRole.getSelectionModel().selectLast();
    }

    public void backAction(ActionEvent actionEvent) {
        Stage stage = (Stage) idPaneAddUser.getScene().getWindow();
        stage.close();
    }

    public void saveAction(ActionEvent actionEvent) {
        boolean sveOk = true;
        sveOk = isEmpty(fldFirstName);
        sveOk &= isEmpty(fldLastName);
        sveOk &= isEmpty(fldAddress);
        sveOk &= isEmpty(fldEmail);
        sveOk &= isEmpty(fldUsername);
        sveOk &= phoneOk(fldPhone);

        if (!sveOk){
            user = null;
            return;
        }


        new Thread(() -> {
            try {
                JSONObject jo = emailDao.getData(fldEmail.getText());

                fldEmail.getStyleClass().removeAll("ok");
                fldEmail.getStyleClass().removeAll("notOk");
                fldEmail.getStyleClass().add("checking");
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
            JOptionPane.showMessageDialog(null,
                    "Uspješno ste sačuvali korisnika!");
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

    private boolean phoneOk(TextField fldPhone) {
        String regexPattern = "(^060[0-9]{7})|(^06[1-3]{1}[0-9]{6})";
        if (!fldPhone.getText().matches(regexPattern)) {
            fldPhone.getStyleClass().removeAll("ok");
            fldPhone.getStyleClass().add("notOk");
            return false;
        } else {
            fldPhone.getStyleClass().removeAll("notOk");
            fldPhone.getStyleClass().add("ok");
        }
        return true;

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
       if(user != null) {
           int input = JOptionPane.showConfirmDialog(null, "Da li ste sigurni da želite obrisati korisnika?");
           if (input == 0) { //0 = yes, 1 = no, 2 = cancel
               dao.deleteUser(user.getId());
               Stage stage = (Stage) idPaneAddUser.getScene().getWindow();
               stage.close();
           }
           if (input == 1) { //0 = yes, 1 = no, 2 = cancel
               Stage stage = (Stage) idPaneAddUser.getScene().getWindow();
               stage.close();
           }
           if (input == 2) { //0 = yes, 1 = no, 2 = cancel
               Stage stage = (Stage) idPaneAddUser.getScene().getWindow();
               stage.close();
           }
       } else {
           JOptionPane.showMessageDialog(null, "Ne možete obrisati korisnika kojeg niste ni kreirali!");
       }


    }
}
