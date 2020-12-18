package it.unito.prog.lib.utils;

import java.util.regex.Pattern;

public final class MailUtil {
    public static boolean validate(String s) {
        Pattern mailPattern = Pattern.compile("^[a-z0-9_+&*-]+(?:\\." +
                "[a-z0-9_+&*-]+)*@" +
                "(?:[a-z0-9-]+\\.)+[a-z]" +
                "{2,7}$", Pattern.CASE_INSENSITIVE);
        return mailPattern.matcher(s).find();
    }
}
