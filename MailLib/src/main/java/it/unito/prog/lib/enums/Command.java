package it.unito.prog.lib.enums;

public enum Command {
    PING(0),
    GOODBYE(0),
    LOGIN(2),
    REGISTER(2),
    LIST_EMAILS(1),
    CHECK_EMAILS(0),
    SEND_EMAIL(1),
    READ_EMAIL(2),
    DELETE_EMAIL(2),
    RESTORE_EMAIL(1);

    // ---------------------------------------------------------------

    private final int paramsLength;

    Command(int paramsLength) {
        this.paramsLength = paramsLength;
    }

    public static Command fromString(String s) {
        Command command = null;
        try {
            command = Command.valueOf(s.toUpperCase());
        } catch (EnumConstantNotPresentException ignored) {
        }
        return command;
    }

    public int getParamsLength() {
        return this.paramsLength;
    }

    @Override
    public String toString() {
        return this.name().toUpperCase();
    }
}
