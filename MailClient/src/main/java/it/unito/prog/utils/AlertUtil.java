package it.unito.prog.utils;

import javafx.scene.control.Alert;

public final class AlertUtil {

    public static void showInfo(String content) {
        show(Alert.AlertType.INFORMATION, content);
    }

    public static void showError(String content) {
        show(Alert.AlertType.ERROR, content);
    }

    public static void show(Alert.AlertType alertType, String content) {
        show(alertType, null, content);
    }

    public static void show(Alert.AlertType alertType, String header, String content) {
        PlatformUtil.safeRun(() -> {
            Alert alert = new Alert(alertType);
            alert.setTitle(alertType.name());
            alert.setHeaderText(header);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }
}
