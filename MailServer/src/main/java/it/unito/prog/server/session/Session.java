package it.unito.prog.server.session;

import it.unito.prog.lib.enums.ServerResponse;
import it.unito.prog.lib.objects.User;
import org.json.JSONObject;

public interface Session extends Runnable {
    boolean setUser(JSONObject auth);

    boolean isLoggedIn();

    User getUser();

    void sendResponse(ServerResponse serverResponse, String... params);

    void close();
}
