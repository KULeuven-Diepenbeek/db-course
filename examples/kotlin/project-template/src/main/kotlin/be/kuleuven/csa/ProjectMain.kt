package be.kuleuven.csa

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage

var rootStage: Stage? = null

class ProjectMain : Application() {
    override fun start(primaryStage: Stage?) {
        rootStage = primaryStage
        val scene = Scene(loadFXML("/csamain.fxml"))
        with(primaryStage!!) {
            this.scene = scene
            title = "CSA Administratie hoofdscherm"
            show()
        }
    }

    private fun loadFXML(fxml: String): Parent {
        val resource = this::class.java.getResource(fxml)
        return FXMLLoader(resource).load()
    }
}

fun main(args: Array<String>) {
    Application.launch(ProjectMain::class.java, *args)
}