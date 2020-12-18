package it.unito.prog.lib.enums;

public enum ServerResponse {
    OK(null),
    UNKNOWN("Unknown error!"),
    USER_NOT_FOUND("User not found!"),
    CMD_NOT_FOUND("Command not found!"),
    SERVER_OFFLINE("Server seems offline."),
    RESOURCE_NOT_FOUND("Resource not found!"),
    IO_ERROR("Server error during IO operations!"),
    ANONYMOUS_SESSION("Please do sign-in before."),
    WRONG_PASSWORD("Wrong password for this user!"),
    SEND_CMD_FAILED("Error sending command to server!"),
    WRONG_ADDRESS_FORMAT("Invalid email address format."),
    ALREADY_REGISTERED_USER("This user is already registered."),
    SENT_EXCEPTION("Some users may not have received your email."),
    WRONG_EMAIL_VER("You are using an incompatible email class version."),
    MISSING_PARAMS("This command requires parameters that have not been provided.");

    // ---------------------------------------------------------------

    private final String readable;

    ServerResponse(String readable) {
        this.readable = readable;
    }

    public static ServerResponse fromString(String s) {
        ServerResponse serverResponse = null;
        try {
            serverResponse = ServerResponse.valueOf(s.toUpperCase());
        } catch (EnumConstantNotPresentException ignored) {
        }
        return serverResponse;
    }

    @Override
    public String toString() {
        return readable;
    }
}
