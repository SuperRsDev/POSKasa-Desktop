package ba.unsa.etf.nrs.Contollers;

import ba.unsa.etf.nrs.DataClasses.User;
import ba.unsa.etf.nrs.DAO.PosDAO;
import ba.unsa.etf.nrs.Services.AuthService;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ManageUsersController {

    private PosDAO dao;
    public ImageView enFlagImg;
    public ImageView bsFlagImg;
    private static boolean bosnian = false;
    private boolean choosen = false;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public Button btnBack;
    public Button btnAddUser;
    public ListView<User> listUsers;
    public GridPane idPane;

    private User user;


    private AuthService authService;

    public ManageUsersController() {
        this.authService = AuthService.getInstance();
    }

    @FXML
    public void initialize() {
        dao = PosDAO.getInstance();
        // dodavanje korisnika iz baze za kategoriju
        listUsers.setItems(FXCollections.observableArrayList(dao.getUsers()));
        listUsers.refresh();
        listUsers.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent click) {

                if (click.getClickCount() == 2) {
                    User currentUser = listUsers.getSelectionModel()
                            .getSelectedItem();
                    try {
                        ResourceBundle bundle = ResourceBundle.getBundle("Translation");
                        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/addEditUser.fxml"), bundle);
                        AddEditUserController ctrl = new AddEditUserController(currentUser);
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
                }
            }
        });

    }


    private void openGlavna() {
        try {
            ResourceBundle bundle = ResourceBundle.getBundle("Translation");
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/manageUsers.fxml"), bundle);
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

    public void addUserAction(ActionEvent actionEvent) {
        try {
            ResourceBundle bundle = ResourceBundle.getBundle("Translation");
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/addEditUser.fxml"), bundle);
            AddEditUserController ctrl = new AddEditUserController();
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
    }

    public void backAction(ActionEvent actionEvent) {
        Stage stage = (Stage) idPane.getScene().getWindow();
        stage.close();
    }

    public void deleteUserAction(ActionEvent actionEvent) {
        int input = JOptionPane.showConfirmDialog(null, "Da li ste sigurni da želite obrisati korisnika?");
        if (input == 0) { //0 = yes, 1 = no, 2 = cancel
            dao.deleteUser(listUsers.getSelectionModel().getSelectedItem().getId());
            listUsers.refresh();
        }

    }
}
