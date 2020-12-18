package it.unito.prog.lib.enums;

public enum MailFolder {
    INBOX,
    OUTBOX,
    TRASH;

    // ---------------------------------------------------------------

    public static MailFolder fromString(String s) {
        MailFolder mailFolder = null;
        try {
            mailFolder = MailFolder.valueOf(s.toUpperCase());
        } catch (EnumConstantNotPresentException ignored) {
        }
        return mailFolder;
    }

    @Override
    public String toString() {
        return this.name().toUpperCase();
    }
}
