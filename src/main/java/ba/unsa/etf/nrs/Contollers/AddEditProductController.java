package ba.unsa.etf.nrs.Contollers;

import ba.unsa.etf.nrs.DAO.EmailDao;
import ba.unsa.etf.nrs.DAO.PosDAO;
import ba.unsa.etf.nrs.DataClasses.Category;
import ba.unsa.etf.nrs.DataClasses.Product;
import ba.unsa.etf.nrs.DataClasses.Role;
import ba.unsa.etf.nrs.DataClasses.User;
import ba.unsa.etf.nrs.Services.AuthService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AddEditProductController {

    public Button btnBack;
    public Button btnSave;
    public Button btnDelete;
    public Button btnChangePicture;

    public TextField fldName;
    public TextField fldDescription;

    public ChoiceBox<Category> choiceCategory;
    public ChoiceBox<String> choiceStatus;
    public TextField fldUnitPrice;
    public TextField fldQuantity;
    public TextField fldSellingPrice;
    private PosDAO dao;

    private Product product = null;
    private AuthService authService;


    public AddEditProductController(Product product) {
        this.product = product;
        dao = PosDAO.getInstance();
        this.authService = AuthService.getInstance();
    }

    public AddEditProductController() {
        dao = PosDAO.getInstance();
        this.authService = AuthService.getInstance();
    }

    @FXML
    public void initialize() {
        choiceCategory.setItems(FXCollections.observableArrayList(dao.getCategories()));
        List<String> statusList= new ArrayList<>();
        statusList.add("dostupan");
        statusList.add("nedostupan");
        choiceStatus.setItems(FXCollections.observableArrayList(statusList));
        if(product != null) {
            fldName.setText(product.getName());
            fldDescription.setText(product.getDescription());
            Category selectedCategory = dao.getCategoryByName(product.getCategory().getName());
            for (Category choiceCategory : choiceCategory.getItems()) {
                if (choiceCategory.getId() == product.getCategory().getId()) {
                    selectedCategory = choiceCategory;
                }
            }
            fldUnitPrice.setText(String.valueOf(product.getUnitPrice()));
            fldQuantity.setText(String.valueOf(product.getStockQuantity()));
            fldSellingPrice.setText(String.valueOf(product.getSellingPrice()));
            choiceCategory.getSelectionModel().select(selectedCategory);
            choiceStatus.getSelectionModel().select(product.getStatus());
        } else {
            choiceCategory.getSelectionModel().selectFirst();
            choiceStatus.getSelectionModel().selectFirst();
        }
    }

    public void backAction(ActionEvent actionEvent) {
        Stage stage = (Stage) fldName.getScene().getWindow();
        stage.close();
    }

    public void saveAction(ActionEvent actionEvent) {
        boolean sveOk = true;
        sveOk = isEmpty(fldName);
        sveOk &= isEmpty(fldDescription);
        sveOk &= isEmpty(fldQuantity);
        sveOk &= isEmpty(fldSellingPrice);
        sveOk &= isEmpty(fldUnitPrice);

        if (!sveOk){
            product = null;
            return;
        }


        if (product == null) {
            product = new Product();
            product.setName(fldName.getText());
            product.setDescription(fldDescription.getText());
            product.setSellingPrice(Integer.parseInt(fldSellingPrice.getText()));
            product.setStockQuantity(Integer.parseInt(fldQuantity.getText()));
            product.setUnitPrice(Integer.parseInt(fldUnitPrice.getText()));
            product.setCategory(choiceCategory.getValue());
            product.setStatus(choiceStatus.getValue());
            int productId = dao.addProduct(product);
            product.setProductId(productId);
            JOptionPane.showMessageDialog(null,"Uspješno ste sačuvali artikal!");
            Stage stage = (Stage) fldName.getScene().getWindow();
            stage.close();
        } else {
            dao.updateProduct(product.getProductId(), fldName.getText(), Integer.parseInt(fldQuantity.getText()), choiceStatus.getValue(),
                    fldDescription.getText(), Integer.parseInt(fldUnitPrice.getText()), Integer.parseInt(fldSellingPrice.getText()), choiceCategory.getValue());
            JOptionPane.showMessageDialog(null,"Uspješno ste sačuvali artikal!");
            Stage stage = (Stage) fldName.getScene().getWindow();
            stage.close();

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
        if(product != null) {
            int input = JOptionPane.showConfirmDialog(null, "Da li ste sigurni da želite obrisati artikal?");
            if (input == 0) { //0 = yes, 1 = no, 2 = cancel
                dao.deleteProduct(product.getProductId());
                Stage stage = (Stage) fldName.getScene().getWindow();
                stage.close();
            }
            if (input == 1) { //0 = yes, 1 = no, 2 = cancel
                Stage stage = (Stage) fldName.getScene().getWindow();
                stage.close();
            }
            if (input == 2) { //0 = yes, 1 = no, 2 = cancel
                Stage stage = (Stage) fldName.getScene().getWindow();
                stage.close();
            }
        } else {
            JOptionPane.showMessageDialog(null, "Ne možete obrisati artikal kojeg niste ni kreirali!");
        }
    }

}
