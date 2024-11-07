package vn.edu.usth.outlook.sent;

// MessageStore.java
import java.util.ArrayList;
import java.util.List;

public class MessageStore {
    private static MessageStore instance;
    private List<Message> sentMessages;

    private MessageStore() {
        sentMessages = new ArrayList<>();
    }

    public static synchronized MessageStore getInstance() {
        if (instance == null) {
            instance = new MessageStore();
        }
        return instance;
    }

    public void addMessage(Message message) {
        sentMessages.add(message);
    }

    public List<Message> getSentMessages() {
        return new ArrayList<>(sentMessages);
    }
}
