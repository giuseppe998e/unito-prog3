package it.unito.prog.controllers;

import it.unito.prog.controllers.components.ClientStatusLabel;
import it.unito.prog.controllers.components.ServerStatusDot;
import it.unito.prog.lib.objects.Email;
import it.unito.prog.scene.AppScene;
import it.unito.prog.utils.PlatformUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.shape.Circle;
import javafx.scene.web.WebView;

import java.net.URL;
import java.util.ResourceBundle;

public final class ReadController extends BaseController {
    private Email currentEmail;

    @FXML
    private TextField senderField;

    @FXML
    private TextField recipientField;

    @FXML
    private TextField subjectField;

    @FXML
    private WebView htmlViewer;

    @FXML
    private Label clientStatusLabel;

    @FXML
    private Circle serverStatusCircle;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Init server status dot
        ServerStatusDot.bind(serverStatusCircle);

        // Init status label
        ClientStatusLabel.bind(clientStatusLabel);
    }

    public void loadEmailView(Email email) {
        currentEmail = email;
        PlatformUtil.safeRun(() -> {
            senderField.setText(email.getSender());
            recipientField.setText(String.join(", ", email.getRecipients()));
            subjectField.setText(email.getSubject());
            htmlViewer.getEngine().loadContent(email.getBody());
        });
    }

    @FXML
    public void forwardEmail(ActionEvent actionEvent) {
        switchToCompose(0);
    }

    @FXML
    public void replyEmail(ActionEvent actionEvent) {
        switchToCompose(1);
    }

    @FXML
    public void replyAllEmail(ActionEvent actionEvent) {
        switchToCompose(2);
    }

    @FXML
    public void closeAction(ActionEvent actionEvent) {
        currentEmail = null;
        senderField.setText("");
        recipientField.setText("");
        subjectField.setText("");
        htmlViewer.getEngine().loadContent("");

        sceneSwitcher().switchTo(AppScene.MAIN);
        setStatusLabel(BaseController.waitingForInput);
    }

    private void switchToCompose(int mode) {
        ComposeController composeController = (ComposeController) sceneSwitcher().getController(AppScene.COMPOSE);
        composeController.loadEmailView(currentEmail, mode);
        sceneSwitcher().switchTo(AppScene.COMPOSE);
    }
}
