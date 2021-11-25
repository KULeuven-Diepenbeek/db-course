package be.kuleuven.pccustomizer.controller;

import be.kuleuven.pccustomizer.ProjectMain;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ProjectMainController {

    @FXML
    private Button btnComputerConfigs;
    @FXML
    private Button btnHwComponenten;
    @FXML
    private Button btnConfigAttaches;

    public void initialize() {
        btnHwComponenten.setOnAction(e -> showBeheerScherm("hwcomponenten"));
        btnComputerConfigs.setOnAction(e -> showBeheerScherm("computerconfigs"));
        btnConfigAttaches.setOnAction(e -> showBeheerScherm("attaches"));
    }

    private void showBeheerScherm(String id) {
        var resourceName = "beheer" + id + ".fxml";
        try {
            var stage = new Stage();
            var root = (AnchorPane) FXMLLoader.load(getClass().getClassLoader().getResource(resourceName));
            var scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Admin " + id);
            stage.initOwner(ProjectMain.getRootStage());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.show();

        } catch (Exception e) {
            throw new RuntimeException("Kan beheerscherm " + resourceName + " niet vinden", e);
        }
    }
}
