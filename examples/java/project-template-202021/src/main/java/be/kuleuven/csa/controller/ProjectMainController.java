package be.kuleuven.csa.controller;

import be.kuleuven.csa.ProjectMain;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class ProjectMainController {

    @FXML
    private Button btnBoerderijen;
    @FXML
    private Button btnTips;
    @FXML
    private Button btnKlanten;
    @FXML
    private Button btnInschrijvingen;
    @FXML
    private Button btnProducten;

    public void initialize() {
        btnBoerderijen.setOnAction(e -> showBeheerScherm("boerderijen"));
        btnTips.setOnAction(e -> showBeheerScherm("tips"));
        btnKlanten.setOnAction(e -> showBeheerScherm("klanten"));
        btnInschrijvingen.setOnAction(e -> showBeheerScherm("inschrijvingen"));
        btnProducten.setOnAction(e -> showBeheerScherm("producten"));
    }

    private void showBeheerScherm(String id) {
        var resourceName = "beheer" + id + ".fxml";
        try {
            var stage = new Stage();
            var root = (AnchorPane) FXMLLoader.load(getClass().getClassLoader().getResource(resourceName));
            var scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Beheer van " + id);
            stage.initOwner(ProjectMain.getRootStage());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.show();

        } catch (Exception e) {
            throw new RuntimeException("Kan beheerscherm " + resourceName + " niet vinden", e);
        }
    }
}
