package ba.unsa.etf.nrs.Contollers;

import ba.unsa.etf.nrs.DAO.EmailDao;
import ba.unsa.etf.nrs.DAO.PosDAO;
import ba.unsa.etf.nrs.DataClasses.Role;
import ba.unsa.etf.nrs.DataClasses.User;
import ba.unsa.etf.nrs.PrintReport;
import ba.unsa.etf.nrs.Services.AuthService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import net.sf.jasperreports.engine.JRException;
import org.json.JSONObject;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class ProductSalesReportController {
    public Button btnBack;
    public Button btnPrintNumeric;
    public Button btnPrintGraphic;
    public Label lblTitle;

    private PosDAO dao;
    private AuthService authService;


    @FXML
    public void initialize() {
        lblTitle.setText("%Izvjestaji_o_najprodavanijim_artiklima");
    }

    public void backAction(ActionEvent actionEvent) {
        Stage stage = (Stage) btnBack.getScene().getWindow();
        stage.close();
    }

    public void printNumericAction(ActionEvent actionEvent) {
        try {
            new PrintReport().showReport(dao.getConn(), "salesReport.jrxml");
        } catch (JRException e1) {
            e1.printStackTrace();
        }
    }

    public void printGraphicAction(ActionEvent actionEvent) {
        try {
            new PrintReport().showReport(dao.getConn(), "salesChartReport.jrxml");
        } catch (JRException e1) {
            e1.printStackTrace();
        }
    }
}
