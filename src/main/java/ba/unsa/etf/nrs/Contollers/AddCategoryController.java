package ba.unsa.etf.nrs.Contollers;

import ba.unsa.etf.nrs.DAO.PosDAO;
import ba.unsa.etf.nrs.DataClasses.Category;
import ba.unsa.etf.nrs.Services.AuthService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import javax.swing.*;

public class AddCategoryController {


        public Button btnBack;
        public Button btnSave;
        public Button btnDelete;

        public TextField fldName;
        public TextField fldDescription;
        private PosDAO dao;

        private Category category = null;
        private AuthService authService;


        public AddCategoryController(Category category) {
            this.category = category;
            dao = PosDAO.getInstance();
            this.authService = AuthService.getInstance();
        }

        public AddCategoryController() {
            dao = PosDAO.getInstance();
            this.authService = AuthService.getInstance();
        }

        @FXML
        public void initialize() {
            if(category != null) {
                fldName.setText(category.getName());
                fldDescription.setText(category.getDescription());
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
            if (!sveOk){
                category = null;
                return;
            }
            if (category == null) {
                category = new Category();
                category.setName(fldName.getText());
                category.setDescription(fldDescription.getText());
                int categoryId = dao.addCategory(category);
                category.setId(categoryId);
                JOptionPane.showMessageDialog(null,"Uspješno ste sačuvali kategoriju!");
                Stage stage = (Stage) fldName.getScene().getWindow();
                stage.close();
            } else {
                JOptionPane.showMessageDialog(null,"Kategorija se ne može uređivati!");
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
            if(category != null) {
                int input = JOptionPane.showConfirmDialog(null, "Da li ste sigurni da želite obrisati kategoriju?");
                if (input == 0) { //0 = yes, 1 = no, 2 = cancel
                    dao.deleteCategory(category.getId());
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
                JOptionPane.showMessageDialog(null, "Ne možete obrisati kategoriju kojeg niste ni kreirali!");
            }
        }

    public Category getCategory() {
        return category;
    }
}
