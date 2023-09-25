package be.kuleuven.dbproject.controller;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class BeheerAttachesController {

    @FXML
    private TableView tblTips;

    public void initialize() {
        initTable();
        tblTips.setOnMouseClicked(e -> {
            if(e.getClickCount() == 2 && tblTips.getSelectionModel().getSelectedItem() != null) {
                var selectedRow = (List<String>) tblTips.getSelectionModel().getSelectedItem();
                runResource(selectedRow.get(2));
            }
        });
    }

    private boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().indexOf("win") >= 0;
    }

    private boolean isMac() {
        return System.getProperty("os.name").toLowerCase().indexOf("mac") >= 0;
    }

    private void runResource(String resource) {
        try {
            // TODO dit moet niet van de resource list komen maar van een DB.
            var data = this.getClass().getClassLoader().getResourceAsStream(resource).readAllBytes();
            var path = Paths.get("out-" + resource);
            Files.write(path, data);
            Thread.sleep(1000);

            var process = new ProcessBuilder();

            if(isWindows()) {
                process.command("cmd.exe", "/c", "start " + path.toRealPath().toString());
            } else if(isMac()) {
                process.command("open", path.toRealPath().toString());
            } else {
                throw new RuntimeException("Ik ken uw OS niet jong");
            }

            process.start();
        } catch (Exception e) {
            throw new RuntimeException("resource " + resource + " kan niet ingelezen worden", e);
        }
    }

    private void initTable() {
        tblTips.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        tblTips.getColumns().clear();

        // TODO zijn dit de juiste kolommen?
        int colIndex = 0;
        for(var colName : new String[]{"Attach beschrijving", "Grootte in KB", "Handle"}) {
            TableColumn<ObservableList<String>, String> col = new TableColumn<>(colName);
            final int finalColIndex = colIndex;
            col.setCellValueFactory(f -> new ReadOnlyObjectWrapper<>(f.getValue().get(finalColIndex)));
            tblTips.getColumns().add(col);
            colIndex++;
        }

        // TODO verwijderen en "echte data" toevoegen!
        tblTips.getItems().add(FXCollections.observableArrayList("Mooie muziek om bij te gamen", "240", "attach-dubbelklik-op-mij.mp3"));
    }
}
