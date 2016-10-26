package org.pwr.model;

/**
 * Created by mkonczyk on 2016-10-25.
 */
public class Message {
    private String message;
    private String from;

    public Message(String message, String from) {
        this.from = from;
        this.message = message;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getMsg() {
        return message;
    }

    public void setMsg(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return (from + ": " + message);
    }
}
