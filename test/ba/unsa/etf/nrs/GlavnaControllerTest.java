package ba.unsa.etf.nrs;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.io.File;

import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ApplicationExtension.class)
class GlavnaControllerTest {
    Stage theStage;
    GlavnaController ctrl;

    @Start
    public void start (Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/glavna.fxml"));
        ctrl = new GlavnaController();
        loader.setController(ctrl);
        Parent root = loader.load();
        stage.setTitle("POS Kasa");
        stage.setScene(new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
        stage.setResizable(false);
        stage.show();

        stage.toFront();

        theStage = stage;
    }
}