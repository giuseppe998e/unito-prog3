package it.unito.prog.model.modules.client;

import it.unito.prog.lib.enums.Command;
import it.unito.prog.lib.enums.ServerResponse;
import javafx.beans.property.SimpleObjectProperty;

import java.util.List;

public interface ClientMail {
    void bindServerStatus(SimpleObjectProperty<Boolean> serverStatus);

    void sendCmd(Command command, ResponseHandler responseHandler, String... params);

    void close();

    @FunctionalInterface
    interface ResponseHandler {
        void run(ServerResponse response, List<String> args);
    }
}
