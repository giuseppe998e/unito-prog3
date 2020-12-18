package it.unito.prog.lib.utils;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class HashGen {
    public static String SHA1(String input) {
        return HashGen.hashedStr("SHA-1", input);
    }

    public static String MD5(String input) {
        return HashGen.hashedStr("MD5", input);
    }

    private static String hashedStr(String method, String input) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(method);
            messageDigest.reset();
            messageDigest.update(input.getBytes(StandardCharsets.UTF_8));
            return String.format("%040x", new BigInteger(1, messageDigest.digest()));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
    }
}