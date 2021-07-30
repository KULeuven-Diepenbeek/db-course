package be.kuleuven.csa.controller

import javafx.collections.FXCollections

import javafx.beans.property.ReadOnlyObjectWrapper

import javafx.collections.ObservableList

import javafx.scene.control.SelectionMode

import java.nio.file.Files

import java.nio.file.Paths

import javafx.fxml.FXML
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import java.lang.Exception
import java.util.*


class BeheerTipsController {
    @FXML
    private lateinit var tblTips: TableView<ObservableList<String>>

    fun initialize() {
        initTable()
        tblTips.setOnMouseClicked { e ->
            if (e.getClickCount() === 2 && tblTips.getSelectionModel().getSelectedItem() != null) {
                val selectedRow = tblTips.getSelectionModel().getSelectedItem() as List<String>
                runResource(selectedRow[2])
            }
        }
    }

    private fun isWindows(): Boolean {
        return System.getProperty("os.name").lowercase(Locale.getDefault()).indexOf("win") >= 0
    }

    private fun isMac(): Boolean {
        return System.getProperty("os.name").lowercase(Locale.getDefault()).indexOf("mac") >= 0
    }

    private fun runResource(resource: String) {
        try {
            // TODO dit moet niet van de resource list komen maar van een DB.
            val data = this.javaClass.classLoader.getResourceAsStream(resource).readAllBytes()
            val path = Paths.get("out-$resource")
            Files.write(path, data)
            Thread.sleep(1000)
            val process = ProcessBuilder()
            if (isWindows()) {
                process.command("cmd.exe", "/c", "start " + path.toRealPath().toString())
            } else if (isMac()) {
                process.command("open", path.toRealPath().toString())
            } else {
                throw RuntimeException("Ik ken uw OS niet jong")
            }
            process.start()
        } catch (e: Exception) {
            throw RuntimeException("resource $resource kan niet ingelezen worden", e)
        }
    }

    private fun initTable() {
        tblTips.getSelectionModel().setSelectionMode(SelectionMode.SINGLE)
        tblTips.getColumns().clear()

        // TODO zijn dit de juiste kolommen?
        var colIndex = 0
        for (colName in arrayOf("Tip beschrijving", "Grootte in KB", "Handle")) {
            val col: TableColumn<ObservableList<String>, String> = TableColumn(colName)
            val finalColIndex = colIndex
            col.setCellValueFactory { f -> ReadOnlyObjectWrapper(f.getValue().get(finalColIndex)) }
            tblTips.getColumns().add(col)
            colIndex++
        }

        // TODO verwijderen en "echte data" toevoegen!
        tblTips.getItems().add(
            FXCollections.observableArrayList(
                "Mooie muziek om bij te koken",
                "240",
                "kooktip-dubbelklik-op-mij.mp3"
            )
        )
    }
}