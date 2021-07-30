package be.kuleuven.csa.controller

import javafx.stage.Modality

import javafx.scene.Scene

import javafx.fxml.FXMLLoader

import javafx.scene.layout.AnchorPane

import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.stage.Stage
import java.lang.Exception

import be.kuleuven.csa.rootStage

class ProjectMainController {
    @FXML
    private lateinit var btnBoerderijen: Button

    @FXML
    private lateinit var btnTips: Button

    @FXML
    private lateinit var btnKlanten: Button

    @FXML
    private lateinit var btnInschrijvingen: Button

    @FXML
    private lateinit var btnProducten: Button

    fun initialize() {
        btnBoerderijen.setOnAction { e -> showBeheerScherm("boerderijen") }
        btnTips.setOnAction { e -> showBeheerScherm("tips") }
        btnKlanten.setOnAction { e -> showBeheerScherm("klanten") }
        btnInschrijvingen.setOnAction { e -> showBeheerScherm("inschrijvingen") }
        btnProducten.setOnAction { e -> showBeheerScherm("producten") }
    }

    private fun showBeheerScherm(id: String) {
        val resourceName = "/beheer$id.fxml"
        try {
            val stage = Stage()
            val root = FXMLLoader.load<Any>(this::class.java.getResource(resourceName)) as AnchorPane
            val scene = Scene(root)
            stage.setScene(scene)
            stage.setTitle("Beheer van $id")
            stage.initOwner(rootStage)
            stage.initModality(Modality.WINDOW_MODAL)
            stage.show()
        } catch (e: Exception) {
            throw RuntimeException("Kan beheerscherm $resourceName niet vinden", e)
        }
    }
}