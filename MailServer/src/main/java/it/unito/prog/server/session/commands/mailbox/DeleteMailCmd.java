package it.unito.prog.server.session.commands.mailbox;

import it.unito.prog.Main;
import it.unito.prog.lib.enums.MailFolder;
import it.unito.prog.lib.enums.ServerResponse;
import it.unito.prog.lib.objects.User;
import it.unito.prog.server.session.Session;
import it.unito.prog.server.session.commands.CommandExecutor;

import java.util.List;
import java.util.concurrent.locks.Lock;

public class DeleteMailCmd implements CommandExecutor {
    @Override
    public void handle(Session session, List<String> params) {
        if (!session.isLoggedIn()) {
            session.sendResponse(ServerResponse.ANONYMOUS_SESSION);
            return;
        }

        MailFolder folder = MailFolder.fromString(params.get(0));
        if (folder == null) {
            session.sendResponse(ServerResponse.UNKNOWN);
            return;
        }

        boolean opResult;

        User loggedUser = session.getUser();
        Lock writeLock = Main.appModel().synchronizeManager().getWriteLock(loggedUser.getEmail());

        try {
            writeLock.lock();

            if (folder == MailFolder.TRASH) {
                opResult = Main.appModel().databaseManager().deleteMail(loggedUser.getEmail(), folder, params.get(1));
            } else {
                opResult = Main.appModel().databaseManager().moveMail(loggedUser.getEmail(), folder, MailFolder.TRASH, params.get(1));
            }
        } finally {
            writeLock.unlock();
        }

        session.sendResponse(opResult ? ServerResponse.OK : ServerResponse.IO_ERROR);
    }
}
