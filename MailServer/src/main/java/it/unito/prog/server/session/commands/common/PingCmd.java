package it.unito.prog.server.session.commands.common;

import it.unito.prog.lib.enums.ServerResponse;
import it.unito.prog.server.session.Session;
import it.unito.prog.server.session.commands.CommandExecutor;

import java.util.List;

public class PingCmd implements CommandExecutor {
    @Override
    public void handle(Session session, List<String> params) {
        session.sendResponse(ServerResponse.OK, "PONG");
    }
}
