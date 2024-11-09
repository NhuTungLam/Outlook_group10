package vn.edu.usth.outlook;

import android.widget.ImageView;

public class Email_receiver {
    private int id;
    private String sender;
    private String receiver;
    private String subject;
    private String content;
    private final long timestamp;

    public Email_receiver(int id, String sender, String receiver, String subject, String content, long timestamp) {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.subject = subject;
        this.content = content;
        this.timestamp = timestamp;
    }
    // Getter và Setter cho id
    public Integer getId() {
        return id;
    }
    public long getTimestamp() {
        return timestamp;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    // Getter và Setter cho sender
    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    // Getter và Setter cho subject
    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    // Getter và Setter cho content
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    // Getter và Setter cho receiver
    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }
}
