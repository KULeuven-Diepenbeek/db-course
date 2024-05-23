module be.kuleuven.dbproject.demo {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;

    opens be.kuleuven.dbproject.demo to javafx.fxml;
    exports be.kuleuven.dbproject.demo;
}