package be.kuleuven.javasql.controller;

import be.kuleuven.javasql.Student;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class StudentController {

    @FXML
    private TableView<Student> tblStudent;

    public void initialize() {
        tblStudent.getColumns().clear();
        TableColumn<Student, String> col = new TableColumn<>("Naam");
        col.setCellValueFactory(f -> new ReadOnlyObjectWrapper<>(f.getValue().getNaam()));
        tblStudent.getColumns().add(col);

        tblStudent.getItems().add(new Student("Joske", "Josmans", 124, true));
    }
}
