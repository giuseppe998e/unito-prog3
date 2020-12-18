package it.unito.prog.server.session.commands.mailbox;

import it.unito.prog.Main;
import it.unito.prog.lib.enums.MailFolder;
import it.unito.prog.lib.enums.ServerResponse;
import it.unito.prog.lib.objects.Email;
import it.unito.prog.lib.objects.User;
import it.unito.prog.lib.utils.Serializer;
import it.unito.prog.model.modules.synchronize.SynchronizeManager;
import it.unito.prog.server.session.Session;
import it.unito.prog.server.session.commands.CommandExecutor;

import java.util.List;
import java.util.concurrent.locks.Lock;

public class SendMailCmd implements CommandExecutor {
    @Override
    public void handle(Session session, List<String> params) {
        if (!session.isLoggedIn()) {
            session.sendResponse(ServerResponse.ANONYMOUS_SESSION);
            return;
        }

        User loggedUser = session.getUser();
        SynchronizeManager<String> sm = Main.appModel().synchronizeManager();

        Email email = Serializer.readFromB64(params.get(0));
        if (email == null) {
            session.sendResponse(ServerResponse.WRONG_EMAIL_VER);
            return;
        }

        List<String> recipients = email.getRecipients();

        // Controlla che tutti gli utenti esistano
        for (String recipient : recipients) {
            if (!Main.appModel().databaseManager().checkUser(recipient)) {
                session.sendResponse(ServerResponse.USER_NOT_FOUND, recipient);
                return;
            }
        }

        boolean everyoneReceived = true;
        for (String recipient : recipients) {
            sm.createLock(recipient);
            Lock recipientWL = sm.getWriteLock(recipient);

            try {
                recipientWL.lock();
                boolean recipientReceived = Main.appModel().databaseManager().saveMail(recipient, MailFolder.INBOX, email.setToRead(true));
                everyoneReceived = everyoneReceived && recipientReceived;
            } finally {
                recipientWL.unlock();
                sm.deleteLock(recipient);
            }
        }

        Lock writeLock = sm.getWriteLock(loggedUser.getEmail());
        try {
            writeLock.lock();
            Main.appModel().databaseManager().saveMail(loggedUser.getEmail(), MailFolder.OUTBOX, email.setToRead(false));
        } finally {
            writeLock.unlock();
        }

        session.sendResponse(everyoneReceived ? ServerResponse.OK : ServerResponse.SENT_EXCEPTION);
    }
}
