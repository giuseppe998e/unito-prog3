package it.unito.prog.server.session.commands;

import it.unito.prog.lib.enums.Command;
import it.unito.prog.lib.enums.ServerResponse;
import it.unito.prog.server.session.Session;
import it.unito.prog.server.session.commands.common.GoodbyeCmd;
import it.unito.prog.server.session.commands.common.PingCmd;
import it.unito.prog.server.session.commands.mailbox.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public final class CommandManager {
    public static CommandManager instance;
    private final HashMap<Command, Class<? extends CommandExecutor>> commands;

    public CommandManager() {
        commands = new HashMap<>();

        initCommonCmds();
        initMailboxCmds();
    }

    public static CommandManager getInstance() {
        if (instance == null) instance = new CommandManager();
        return instance;
    }

    private void initCommonCmds() {
        commands.put(Command.PING, PingCmd.class);
        commands.put(Command.GOODBYE, GoodbyeCmd.class);
    }

    private void initMailboxCmds() {
        commands.put(Command.CHECK_EMAILS, CheckMailsCmd.class);
        commands.put(Command.DELETE_EMAIL, DeleteMailCmd.class);
        commands.put(Command.LIST_EMAILS, ListMailsCmd.class);
        commands.put(Command.READ_EMAIL, ReadMailCmd.class);
        commands.put(Command.RESTORE_EMAIL, RestoreMailCmd.class);
        commands.put(Command.SEND_EMAIL, SendMailCmd.class);
    }

    public void handle(Session session, JSONObject request) {
        try {
            // Check "cmd" and "params" variables
            if (!request.has("cmd")) {
                session.sendResponse(ServerResponse.MISSING_PARAMS);
            }

            // Check and get command (class)
            Command command = Command.fromString(request.getString("cmd"));
            if (command == null) {
                session.sendResponse(ServerResponse.CMD_NOT_FOUND);
                return;
            }

            // Check for auth data and login/register
            if (request.has("auth")) {
                JSONObject auth = request.getJSONObject("auth");
                if (!session.setUser(auth)) return;
            }

            // Execute command (class)
            List<String> params = null;
            if (command.getParamsLength() > 0) {
                JSONArray jsonArray = request.getJSONArray("params");
                if (jsonArray == null || jsonArray.length() < command.getParamsLength()) {
                    session.sendResponse(ServerResponse.MISSING_PARAMS);
                    return;
                }
                // From JSONArray to List<String>
                params = jsonArray.toList().stream().map(Object::toString).collect(Collectors.toList());
            }
            newExecutorInstance(command).handle(session, params);
        } catch (Exception e) {
            session.sendResponse(ServerResponse.UNKNOWN);
            e.printStackTrace();
        }
    }

    private CommandExecutor newExecutorInstance(Command cmd) throws Exception {
        Class<? extends CommandExecutor> executorClass = commands.get(cmd);
        return (executorClass.getDeclaredConstructor()).newInstance();
    }
}
