package it.unito.prog.model.modules.database;

import it.unito.prog.lib.enums.MailFolder;
import it.unito.prog.lib.objects.Email;
import it.unito.prog.lib.objects.User;

import java.util.ArrayList;

public interface DatabaseManager {
    boolean saveUser(User user);

    boolean checkUser(String user);

    User readUser(String user);

    boolean saveMail(String user, MailFolder folder, Email email);

    Email readMail(String user, MailFolder folder, String emailUUID);

    boolean moveMail(String user, MailFolder from, MailFolder to, String emailUUID);

    boolean deleteMail(String user, MailFolder folder, String emailUUID);

    ArrayList<Email> listAllEmails(String user, MailFolder folder);

    ArrayList<Email> listEmails(String user, MailFolder folder, int from, int offset);
}
