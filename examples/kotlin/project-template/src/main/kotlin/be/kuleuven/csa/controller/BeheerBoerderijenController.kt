package be.kuleuven.csa.controller

import javafx.collections.FXCollections

import javafx.beans.property.ReadOnlyObjectWrapper

import javafx.collections.ObservableList

import javafx.fxml.FXML
import javafx.scene.control.*
import javafx.stage.Stage


class BeheerBoerderijenController {
    @FXML
    private lateinit var btnDelete: Button

    @FXML
    private lateinit var  btnAdd: Button

    @FXML
    private lateinit var  btnModify: Button

    @FXML
    private lateinit var  btnClose: Button

    @FXML
    private lateinit var  tblBoerderijen: TableView<ObservableList<String>>

    fun initialize() {
        initTable()
        btnAdd.setOnAction { e -> addNewRow() }
        btnModify.setOnAction { e ->
            verifyOneRowSelected()
            modifyCurrentRow()
        }
        btnDelete.setOnAction { e ->
            verifyOneRowSelected()
            deleteCurrentRow()
        }
        btnClose.setOnAction { e ->
            val stage = btnClose.getScene().getWindow() as Stage
            stage.close()
        }
    }

    private fun initTable() {
        tblBoerderijen.getSelectionModel().setSelectionMode(SelectionMode.SINGLE)
        tblBoerderijen.getColumns().clear()

        // TODO verwijderen en "echte data" toevoegen!
        var colIndex = 0
        for (colName in arrayOf("Naam", "Voornaam", "Oppervlakte", "Aantal varkens")) {
            val col: TableColumn<ObservableList<String>, String> = TableColumn(colName)
            val finalColIndex = colIndex
            col.setCellValueFactory { f -> ReadOnlyObjectWrapper(f.getValue().get(finalColIndex)) }
            tblBoerderijen.getColumns().add(col)
            colIndex++
        }
        for (i in 0..9) {
            tblBoerderijen.getItems().add(
                FXCollections.observableArrayList(
                    "Boer $i",
                    "Jozef V$i",
                    (i * 10).toString(),
                    (i * 33).toString()
                )
            )
        }
    }

    private fun addNewRow() {}

    private fun deleteCurrentRow() {}

    private fun modifyCurrentRow() {}

    fun showAlert(title: String?, content: String?) {
        val alert = Alert(Alert.AlertType.WARNING)
        alert.title = title
        alert.headerText = title
        alert.contentText = content
        alert.showAndWait()
    }

    private fun verifyOneRowSelected() {
        if (tblBoerderijen.getSelectionModel().getSelectedCells().size === 0) {
            showAlert("Hela!", "Eerst een boer selecteren hé.")
        }
    }
}