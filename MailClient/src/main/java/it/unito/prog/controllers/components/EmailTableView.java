package it.unito.prog.controllers.components;

import it.unito.prog.Main;
import it.unito.prog.lib.objects.Email;
import it.unito.prog.model.modules.observables.Observables;
import javafx.collections.ObservableList;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.List;

public class EmailTableView {
    public static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");

    public static void bind(TableView<Email> emailTableView) {
        ObservableList<Email> emailList = Main.appModel().observablesManager().getList(Observables.EMAIL_LIST);
        emailTableView.setItems(emailList);

        emailTableView.getColumns().add(newSubjectColumn());
        emailTableView.getColumns().add(newSenderColumn());
        emailTableView.getColumns().add(newRecipientsColumn());

        TableColumn<Email, Long> dateColumn = newDateColumn();
        emailTableView.getColumns().add(dateColumn);
        emailTableView.getSortOrder().add(dateColumn);
    }

    public static TableColumn<Email, String> newSubjectColumn() {
        TableColumn<Email, String> tableColumn = new TableColumn<>("Subject");
        tableColumn.setCellValueFactory(new PropertyValueFactory<>("subject"));
        return tableColumn;
    }

    public static TableColumn<Email, String> newSenderColumn() {
        TableColumn<Email, String> tableColumn = new TableColumn<>("Sender");
        tableColumn.setCellValueFactory(new PropertyValueFactory<>("sender"));
        return tableColumn;
    }

    public static TableColumn<Email, List<String>> newRecipientsColumn() {
        TableColumn<Email, List<String>> tableColumn = new TableColumn<>("Recipient(s)");
        tableColumn.setCellValueFactory(new PropertyValueFactory<>("recipients"));
        tableColumn.setCellFactory(param -> new TableCell<>() {
            @Override
            protected void updateItem(List<String> item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : String.join(", ", item));
            }
        });
        return tableColumn;
    }

    public static TableColumn<Email, Long> newDateColumn() {
        TableColumn<Email, Long> tableColumn = new TableColumn<>("Date");
        tableColumn.setCellValueFactory(new PropertyValueFactory<>("dateSent"));
        tableColumn.setComparator(Long::compareTo);
        tableColumn.setSortType(TableColumn.SortType.DESCENDING);
        tableColumn.setCellFactory(param -> new TableCell<>() {
            @Override
            protected void updateItem(Long item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : simpleDateFormat.format(Date.from(Instant.ofEpochSecond(item))));
            }
        });
        return tableColumn;
    }
}
