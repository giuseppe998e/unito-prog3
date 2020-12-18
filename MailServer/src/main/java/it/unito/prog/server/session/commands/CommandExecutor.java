package it.unito.prog.server.session.commands;

import it.unito.prog.server.session.Session;

import java.util.List;

public interface CommandExecutor {
    void handle(Session session, List<String> params);
}
