package it.unito.prog.controllers.components;

import it.unito.prog.Main;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

public class ConsoleListView {
    public static void bind(ListView<String> consoleListView) {
        ObservableList<String> logList = Main.appModel().observablesManager().getList("console_log");

        consoleListView.setItems(logList);
        consoleListView.setCellFactory(param -> new ListCell<>() { // List Item Text Wrap
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    setText(null);
                } else {
                    setPrefWidth(param.getWidth() - 10);
                    setWrapText(true);
                    setText(item);
                }
            }
        });

        // Auto scroll of console log
        logList.addListener((ListChangeListener<String>) c -> {
            c.next();
            int size = consoleListView.getItems().size();
            if (size > 0) consoleListView.scrollTo(size - 1);
        });
    }
}
