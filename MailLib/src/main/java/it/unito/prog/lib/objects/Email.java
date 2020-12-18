package it.unito.prog.lib.objects;

import it.unito.prog.lib.utils.HashGen;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public final class Email implements Serializable, Cloneable {
    private static final long serialVersionUID = 4418908782121392803L;

    /** An identical "uuid" does not ensure that the "body" is also the same, use "equals()" method to be sure */
    private final String uuid;
    private final String sender;
    private final List<String> recipients;
    private final Long dateSent;
    private final String subject;

    private String body;
    private boolean toRead;

    public Email(String sender, List<String> recipients, Long dateSent, String subject, String body) {
        this(sender, recipients, dateSent, subject);
        this.body = body;
    }

    public Email(String sender, List<String> recipients, Long dateSent, String subject) {
        this.sender = sender;
        this.recipients = recipients;
        this.dateSent = dateSent;
        this.subject = subject;
        this.toRead = true;
        this.uuid = HashGen.MD5(this.sender + '$' + String.join("$", this.recipients) + '$' + this.dateSent + '$' + this.subject).toUpperCase();
    }

    public String getSender() {
        return sender;
    }

    public List<String> getRecipients() {
        return recipients;
    }

    public Long getDateSent() {
        return dateSent;
    }

    public String getSubject() {
        return subject;
    }

    public String getUUID() {
        return uuid;
    }

    public String getBody() {
        return body;
    }

    public Email setBody(String s) {
        body = s;
        return this;
    }

    public boolean hasBeenRead() {
        return !toRead;
    }

    public Email setToRead(boolean b) {
        toRead = b;
        return this;
    }

    public Email liteClone() { // No BODY
        return new Email(sender, recipients, dateSent, subject)
                .setToRead(toRead);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Email)) return false;
        Email email = (Email) obj;
        return uuid.equals(email.uuid) && Objects.equals(body, email.body);
    }

    @Override
    public String toString() {
        return "Email{Sender='" + sender +
                "', Recipient=['" + String.join("', '", recipients) +
                "'], Subject='" + subject +
                "', Body='" + body +
                "', Date='" + dateSent +
                "', UUID='" + uuid + "'}";
    }
}
