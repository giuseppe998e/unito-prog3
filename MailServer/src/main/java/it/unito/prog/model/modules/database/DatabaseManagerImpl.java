package it.unito.prog.model.modules.database;

import it.unito.prog.lib.enums.MailFolder;
import it.unito.prog.lib.objects.Email;
import it.unito.prog.lib.objects.User;
import it.unito.prog.lib.utils.Serializer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

class DatabaseManagerImpl implements DatabaseManager {
    private final File dbPath;

    public DatabaseManagerImpl(String dbPath) {
        this.dbPath = new File(dbPath);

        if (!(this.dbPath.mkdir() || this.dbPath.exists())) // ! && !
            throw new RuntimeException("Unable to create DB folder!");
        if (!(this.dbPath.canWrite() && this.dbPath.canRead())) // ! || !
            throw new RuntimeException("The DB folder permissions are wrong!");
    }

    @Override
    public boolean saveUser(User user) {
        if (user == null) return false;

        File uPath = new File(dbPath, user.getEmail());
        File uFile = new File(uPath, "user.dat");

        if (uFile.exists()) return true;

        File inbox = new File(uPath, MailFolder.INBOX.toString());
        File sent = new File(uPath, MailFolder.OUTBOX.toString());
        File trash = new File(uPath, MailFolder.TRASH.toString());

        if (!uPath.mkdir()) return false;
        if (!(inbox.mkdir() && sent.mkdir() && trash.mkdir())) return false;
        return Serializer.writeToFile(uFile, user);
    }

    @Override
    public boolean checkUser(String user) {
        File uPath = new File(dbPath, user);
        File uInfo = new File(uPath, "user.dat");

        return uInfo.exists();
    }

    @Override
    public User readUser(String user) {
        File uPath = new File(dbPath, user);
        File userInfo = new File(uPath, "user.dat");

        return userInfo.exists() ? Serializer.readFromFile(userInfo) : null;
    }

    @Override
    public boolean saveMail(String user, MailFolder folder, Email email) {
        File folderPath = getUserFolder(user, folder);
        if (folderPath == null) return false;

        return Serializer.writeToFile(new File(folderPath, email.getUUID() + ".dat"), email);
    }

    @Override
    public Email readMail(String user, MailFolder folder, String emailUUID) {
        File folderPath = getUserFolder(user, folder);
        if (folderPath == null) return null;

        return (Email) Serializer.readFromFile(new File(folderPath, emailUUID + ".dat"));
    }

    @Override
    public boolean moveMail(String user, MailFolder from, MailFolder to, String emailUUID) {
        try {
            String emailFileName = emailUUID + ".dat";

            Path pathFrom = new File(getUserFolder(user, from), emailFileName).toPath();
            Path pathTo = new File(getUserFolder(user, to), emailFileName).toPath();

            Files.move(pathFrom, pathTo, StandardCopyOption.ATOMIC_MOVE);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteMail(String user, MailFolder folder, String emailUUID) {
        File folderPath = getUserFolder(user, folder);
        File mailToDelete = new File(folderPath, emailUUID + ".dat");

        return mailToDelete.delete();
    }

    @Override
    public ArrayList<Email> listAllEmails(String user, MailFolder folder) {
        return listEmails(user, folder, 0, Integer.MAX_VALUE);
    }

    @Override
    public ArrayList<Email> listEmails(String user, MailFolder folder, int from, int offset) {
        File folderPath = getUserFolder(user, folder);
        if (folderPath == null) return null;

        File[] files = folderPath.listFiles();
        if (files == null) return null; // required by Arrays.sort()

        Arrays.sort(files, Comparator.comparingLong(File::lastModified));
        ArrayList<Email> result = new ArrayList<>();

        int minTo = Math.min(from + offset, files.length);
        for (int i = from; i < minTo; i++) {
            Email e = Serializer.readFromFile(files[i]);
            if (e != null) result.add(e.liteClone());
        }

        return result;
    }

    private File getUserFolder(String user, MailFolder folder) {
        File uPath = new File(dbPath, user);
        File folderPath = new File(uPath, folder.toString());

        return folderPath.exists() ? folderPath : null;
    }
}