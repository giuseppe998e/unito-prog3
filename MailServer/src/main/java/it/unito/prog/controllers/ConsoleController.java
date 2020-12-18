package it.unito.prog.controllers;

import it.unito.prog.controllers.components.ConsoleListView;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;

import java.net.URL;
import java.util.ResourceBundle;

public final class ConsoleController implements Initializable {
    @FXML
    private ListView<String> consoleListView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Console ListView - Log
        ConsoleListView.bind(consoleListView);
    }
}
