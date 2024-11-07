package vn.edu.usth.outlook.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

import vn.edu.usth.outlook.R;
import vn.edu.usth.outlook.sent.Message;
import vn.edu.usth.outlook.sent.MessageAdapter;
import vn.edu.usth.outlook.sent.MessageStore;

public class SentFragment extends Fragment {

    private RecyclerView recyclerView;
    private MessageAdapter messageAdapter;
    private View placeholderLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sent, container, false);

        // Initialize RecyclerView and placeholder layout
        recyclerView = view.findViewById(R.id.recycler_sent);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Access the entire placeholder layout
        placeholderLayout = view.findViewById(R.id.placeholder_layout);
        // Add test messages to MessageStore for testing
        MessageStore messageStore = MessageStore.getInstance();
        messageStore.addMessage(new Message("Duong@example.com", "Test Subject 1", "This is a sample content for test message 1", "Nov 11, 2024"));
        messageStore.addMessage(new Message("Duong@example.com", "Test Subject 2", "This is a sample content for test message 2", "Nov 12, 2024"));


        // Fetch sent messages
        List<Message> sentMessages = MessageStore.getInstance().getSentMessages();

        // Check if there are messages to display
        if (sentMessages.isEmpty()) {
            // No messages, show placeholder
            recyclerView.setVisibility(View.GONE);
            placeholderLayout.setVisibility(View.VISIBLE);
        } else {
            // Messages are available, display them
            messageAdapter = new MessageAdapter(sentMessages);
            recyclerView.setAdapter(messageAdapter);

            // Show RecyclerView, hide placeholder
            recyclerView.setVisibility(View.VISIBLE);
            placeholderLayout.setVisibility(View.GONE);
        }

        return view;
    }
}
