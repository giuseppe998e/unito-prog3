package it.unito.prog.server.session.commands.mailbox;

import it.unito.prog.Main;
import it.unito.prog.lib.enums.MailFolder;
import it.unito.prog.lib.enums.ServerResponse;
import it.unito.prog.lib.objects.Email;
import it.unito.prog.lib.objects.User;
import it.unito.prog.lib.utils.Serializer;
import it.unito.prog.server.session.Session;
import it.unito.prog.server.session.commands.CommandExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;

public class ListMailsCmd implements CommandExecutor {
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

        ArrayList<Email> emails;

        User loggedUser = session.getUser();
        Lock readLock = Main.appModel().synchronizeManager().getReadLock(loggedUser.getEmail());

        try {
            readLock.lock();
            emails = Main.appModel().databaseManager().listAllEmails(loggedUser.getEmail(), folder);
        } finally {
            readLock.unlock();
        }

        session.sendResponse(ServerResponse.OK, Serializer.writeToB64(emails));
    }
}
