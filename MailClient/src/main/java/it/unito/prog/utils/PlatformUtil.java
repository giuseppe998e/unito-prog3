package it.unito.prog.utils;

import javafx.application.Platform;

public final class PlatformUtil {
    public static void safeRun(Runnable r) {
        if (Platform.isFxApplicationThread()) r.run();
        else Platform.runLater(r);
    }
}
