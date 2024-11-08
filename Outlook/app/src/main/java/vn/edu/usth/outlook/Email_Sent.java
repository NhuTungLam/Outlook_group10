package vn.edu.usth.outlook;

import android.widget.ImageView;

public class Email_Sent {
    private Integer id;
    private String sender;
    private String subject;
    private String content;
    private String receiver;
    private ImageView profile;

    // Default constructor
    public Email_Sent() {
        // Default constructor required for Firebase
    }

    // Full constructor with id
    public Email_Sent(Integer id, String sender, String receiver, String subject, String content) {
        this.id = id;
        this.sender = sender;
        this.subject = subject;
        this.content = content;
        this.receiver = receiver;
    }

    // Getter and Setter for id
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    // Getter and Setter for sender
    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    // Getter and Setter for subject
    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    // Getter and Setter for content
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    // Getter and Setter for receiver
    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }
}
