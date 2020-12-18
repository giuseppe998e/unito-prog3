package it.unito.prog.model.modules.client;

import it.unito.prog.lib.enums.Command;
import it.unito.prog.lib.enums.ServerResponse;
import it.unito.prog.lib.utils.HashGen;
import it.unito.prog.model.modules.config.ConfigManager;
import it.unito.prog.utils.AlertUtil;
import javafx.beans.property.SimpleObjectProperty;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

class ClientMailImpl implements ClientMail {
    private final static int threadPoolSize = 3;

    private final String address;
    private final int port;

    private final JSONObject auth;
    private final ExecutorService threadPool;

    private SimpleObjectProperty<Boolean> serverStatusBool;

    public ClientMailImpl(ConfigManager configManager) {
        address = configManager.read("server.host");
        port = Integer.parseInt(configManager.read("server.port"));

        auth = new JSONObject()
                .put("email", configManager.read("user.email"))
                .put("password", HashGen.SHA1(configManager.read("user.password")));

        threadPool = Executors.newFixedThreadPool(threadPoolSize);
        serverStatusBool = null;
    }

    @Override
    public void bindServerStatus(SimpleObjectProperty<Boolean> serverStatusBool) {
        this.serverStatusBool = serverStatusBool;
        serverStatusBool.setValue(false);
    }

    @Override
    public void sendCmd(Command command, ResponseHandler responseHandler, String... params) {
        JSONObject request = new JSONObject()
                .put("cmd", command.name())
                .put("params", params)
                .put("auth", auth);

        // Async Task
        threadPool.execute(() -> {
            try {
                Socket connection = openConnection();
                if (serverStatusBool != null) serverStatusBool.set(true);

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                     PrintWriter writer = new PrintWriter(connection.getOutputStream(), true)) {

                    // Send Request
                    writer.println(request.toString());

                    // Read Response
                    String response = reader.readLine();
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        ServerResponse status = ServerResponse.fromString(jsonResponse.getString("status"));

                        List<String> srvArgs = null;
                        if (jsonResponse.has("args")) {
                            JSONArray jsonArray = jsonResponse.getJSONArray("args");
                            // From JSONArray to List<String>
                            srvArgs = jsonArray.toList().stream().map(Object::toString).collect(Collectors.toList());
                        }

                        responseHandler.run(status, srvArgs);
                    } catch (JSONException e) {
                        System.out.println("Error: [Response:'" + response + "', Error: '" + e.getLocalizedMessage() + "']");
                        AlertUtil.showError("Received a not valid response!");
                    }
                }
                connection.close();
            } catch (IOException ignored) {
                /* Server offline */
                if (serverStatusBool != null) serverStatusBool.set(false);
                responseHandler.run(ServerResponse.SERVER_OFFLINE, null);
            }
        });
    }

    @Override
    public void close() {
        try {
            threadPool.shutdown();
            if (!threadPool.awaitTermination(threadPoolSize * 2, TimeUnit.SECONDS)) { // Wait 2sec for thread
                threadPool.shutdownNow();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private Socket openConnection() throws IOException {
        return new Socket(address, port);
    }
}
