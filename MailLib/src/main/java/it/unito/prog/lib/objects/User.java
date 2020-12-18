package it.unito.prog.lib.objects;

import it.unito.prog.lib.utils.HashGen;

import java.io.Serializable;

public final class User implements Serializable {
    private static final long serialVersionUID = -483573783525668602L;

    private final String uuid;
    private final String email;
    private final String password;

    public User(String email, String password) {
        this.email = email;
        this.password = password;
        this.uuid = HashGen.MD5(this.email + '$' + this.password).toUpperCase();
    }

    public String getUUID() {
        return uuid;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof User)) return false;
        User user = (User) obj;
        return uuid.equals(user.uuid);
    }

    @Override
    public String toString() {
        return "User{Email='" + email +
                "', Password='" + password +
                "', UUID='" + uuid + "'}";
    }
}
