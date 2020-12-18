package it.unito.prog.server.session.commands.mailbox;

import it.unito.prog.Main;
import it.unito.prog.lib.enums.MailFolder;
import it.unito.prog.lib.enums.ServerResponse;
import it.unito.prog.lib.objects.Email;
import it.unito.prog.lib.objects.User;
import it.unito.prog.lib.utils.Serializer;
import it.unito.prog.server.session.Session;
import it.unito.prog.server.session.commands.CommandExecutor;

import java.util.List;
import java.util.concurrent.locks.Lock;

public class ReadMailCmd implements CommandExecutor {
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

        User loggedUser = session.getUser();
        Lock readLock = Main.appModel().synchronizeManager().getReadLock(loggedUser.getEmail());

        Email email;
        try {
            readLock.lock();
            email = Main.appModel().databaseManager().readMail(loggedUser.getEmail(), folder, params.get(1));
        } finally {
            readLock.unlock();
        }

        if (email == null) {
            session.sendResponse(ServerResponse.RESOURCE_NOT_FOUND);
            return;
        }

        if (!email.hasBeenRead()) {
            Lock writeLock = Main.appModel().synchronizeManager().getWriteLock(loggedUser.getEmail());
            email.setToRead(false);

            try {
                writeLock.lock();
                Main.appModel().databaseManager().saveMail(loggedUser.getEmail(), folder, email);
            } finally {
                writeLock.unlock();
            }
        }

        session.sendResponse(ServerResponse.OK, Serializer.writeToB64(email));
    }
}
