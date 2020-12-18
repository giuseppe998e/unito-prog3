package it.unito.prog.controllers;

import it.unito.prog.controllers.components.ClientStatusLabel;
import it.unito.prog.controllers.components.EmailTableView;
import it.unito.prog.controllers.components.ServerStatusDot;
import it.unito.prog.lib.enums.Command;
import it.unito.prog.lib.enums.MailFolder;
import it.unito.prog.lib.enums.ServerResponse;
import it.unito.prog.lib.objects.Email;
import it.unito.prog.lib.utils.MailUtil;
import it.unito.prog.lib.utils.Serializer;
import it.unito.prog.model.modules.client.ClientMail;
import it.unito.prog.model.modules.observables.Observables;
import it.unito.prog.scene.AppScene;
import it.unito.prog.utils.AlertUtil;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.shape.Circle;

import java.net.URL;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;

public final class MainController extends BaseController {
    @FXML
    private Button inboxFolderBtn;

    @FXML
    private Button outboxFolderBtn;

    @FXML
    private Button trashFolderBtn;

    @FXML
    private TableView<Email> emailTableView;

    @FXML
    private Label clientStatusLabel;

    @FXML
    private Circle serverStatusCircle;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Check for config.properties "user.email" value
        if (!MailUtil.validate(getCurrentUser())) {
            AlertUtil.showError("The configuration file contains an invalid email, correct the error and restart the application.");
            System.exit(1);
        }

        // Set CurrentFolder listener
        SimpleObjectProperty<MailFolder> currentFolder = observablesManager().getObject(Observables.CURRENT_FOLDER);
        currentFolder.addListener((observable, oldValue, newValue) -> {
            inboxFolderBtn.setDisable(newValue == MailFolder.INBOX);
            outboxFolderBtn.setDisable(newValue == MailFolder.OUTBOX);
            trashFolderBtn.setDisable(newValue == MailFolder.TRASH);
            loadEmailList(newValue);
        });

        // Init emails tableview
        EmailTableView.bind(emailTableView);
        emailTableView.setRowFactory(param -> new TableRow<>() {
            @Override
            protected void updateItem(Email item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) return;

                // Row style (Email readed or not)
                setStyle(item.hasBeenRead() ? "" : "-fx-font-weight: bold");

                // Row tooltip
                String tooltipDate = EmailTableView.simpleDateFormat.format(Date.from(Instant.ofEpochSecond(item.getDateSent())));
                Tooltip rowToolTip = new Tooltip("Subject: " + item.getSubject()
                        + "\nSender: " + item.getSender()
                        + "\nRecipients: " + String.join(", ", item.getRecipients())
                        + "\nDate: " + tooltipDate);
                rowToolTip.setStyle("-fx-font-weight: normal");
                setTooltip(rowToolTip);

                // Row context menu
                setContextMenu(newContextMenu(item));

                // Row double click
                setOnMouseClicked(event -> {
                    if (event.getButton() != MouseButton.PRIMARY) return;
                    if (event.getClickCount() < 2) return;
                    Email email = emailTableView.getSelectionModel().getSelectedItem();
                    loadEmail(email.getUUID(), (response, args) -> {
                        if (response != ServerResponse.OK) {
                            setStatusLabel(response.toString());
                            return;
                        }
                        ReadController composeController = (ReadController) sceneSwitcher().getController(AppScene.READ);
                        composeController.loadEmailView(Serializer.readFromB64(args.get(0)));

                        sceneSwitcher().switchTo(AppScene.READ);
                        setStatusLabel(BaseController.waitingForInput);

                        setStyle("");
                        getItem().setToRead(false);
                    });
                });
            }
        });

        // Init server status dot
        ServerStatusDot.bind(serverStatusCircle);

        // Init status label
        ClientStatusLabel.bind(clientStatusLabel);

        // Load emails from server
        loadEmailList(getCurrentFolder());
    }

    @FXML
    public void changeFolder(ActionEvent actionEvent) {
        Button button = (Button) actionEvent.getSource();
        observablesManager().setObjectValue(Observables.CURRENT_FOLDER, MailFolder.fromString(button.getText()));
    }

    @FXML
    public void refreshAction(ActionEvent actionEvent) {
        loadEmailList(getCurrentFolder());
    }

    @FXML
    public void composeAction(ActionEvent actionEvent) {
        sceneSwitcher().switchTo(AppScene.COMPOSE);
        setStatusLabel(BaseController.waitingForInput);
    }

    private ContextMenu newContextMenu(Email item) {
        ContextMenu cm = new ContextMenu();
        cm.setStyle("-fx-font-weight: normal");

        // MenuItem Forward
        MenuItem miForward = new MenuItem("Forward");
        miForward.setOnAction(event -> loadEmailToCompose(item.getUUID(), 0));

        // MenuItem Reply
        MenuItem miReply = new MenuItem("Reply");
        miReply.setOnAction(event -> loadEmailToCompose(item.getUUID(), 1));

        // MenuItem Reply All
        MenuItem miReplyTo = new MenuItem("Reply All");
        miReplyTo.setOnAction(event -> loadEmailToCompose(item.getUUID(), 2));

        // MenuItem Delete
        MenuItem miDelete = new MenuItem("Delete");
        miDelete.setOnAction(event ->
                clientMail().sendCmd(Command.DELETE_EMAIL, (response, args) -> {
                    if (response != ServerResponse.OK) {
                        setStatusLabel(response.toString());
                        return;
                    }

                    observablesManager().removeListEntry(Observables.EMAIL_LIST, item);
                    setStatusLabel("Email deleted.");
                }, getCurrentFolder().name(), item.getUUID()));

        // MenuItem Restore
        MenuItem miRestore = new MenuItem("Restore");
        miRestore.setOnAction(event ->
                clientMail().sendCmd(Command.RESTORE_EMAIL, (response, args) -> {
                    if (response != ServerResponse.OK) {
                        setStatusLabel(response.toString());
                        return;
                    }

                    observablesManager().removeListEntry(Observables.EMAIL_LIST, item);
                    setStatusLabel("Email deleted.");
                }, item.getUUID()));

        // Choose which items add to ContextMenu
        cm.getItems().addAll(miForward, miReply, miReplyTo, miDelete);
        if (getCurrentFolder() == MailFolder.TRASH) {
            cm.getItems().add(miRestore);
        }

        return cm;
    }

    private void loadEmailToCompose(String emailUUID, int mode) {
        loadEmail(emailUUID, (response, args) -> {
            if (response != ServerResponse.OK) {
                setStatusLabel(response.toString());
                return;
            }

            ComposeController composeController = (ComposeController) sceneSwitcher().getController(AppScene.COMPOSE);
            composeController.loadEmailView(Serializer.readFromB64(args.get(0)), mode);
            sceneSwitcher().switchTo(AppScene.COMPOSE);
        });
    }

    private void loadEmail(String emailUUID, ClientMail.ResponseHandler responseHandler) {
        clientMail().sendCmd(Command.READ_EMAIL, responseHandler, getCurrentFolder().name(), emailUUID);
    }

    private void loadEmailList(MailFolder folder) {
        setStatusLabel("Loading...");
        observablesManager().clearList(Observables.EMAIL_LIST);

        clientMail().sendCmd(Command.LIST_EMAILS, (response, args) -> {
            if (response != ServerResponse.OK) {
                setStatusLabel(response.toString());
                AlertUtil.showError(response.toString());
                return;
            }

            ArrayList<Email> retrieved = Serializer.readFromB64(args.get(0));
            observablesManager().fillList(Observables.EMAIL_LIST, retrieved);
            setStatusLabel("Emails list loaded!");
        }, folder.name());
    }
}
