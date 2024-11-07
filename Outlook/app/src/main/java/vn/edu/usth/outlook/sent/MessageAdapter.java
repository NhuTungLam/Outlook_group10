package vn.edu.usth.outlook.sent;

// MessageAdapter.java
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

import vn.edu.usth.outlook.R;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Message> messages;

    // Constructor to accept the list of messages
    public MessageAdapter(List<Message> messages) {
        this.messages = messages;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the item_inbox layout for each message item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_inbox, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        // Get the current message
        Message message = messages.get(position);

        // Bind data from Message object to the views in item_inbox.xml
        holder.nameTextView.setText(message.getTo());          // Set recipient's name or email
        holder.subjectTextView.setText(message.getSubject());   // Set email subject
        holder.contentTextView.setText(message.getContent());   // Set email content preview
        holder.dateTextView.setText(message.getTimestamp());    // Set timestamp of the message
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    // ViewHolder to hold references to each view in item_inbox.xml
    static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, subjectTextView, contentTextView, dateTextView;

        MessageViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.name);
            subjectTextView = itemView.findViewById(R.id.head_email);
            contentTextView = itemView.findViewById(R.id.content);
            dateTextView = itemView.findViewById(R.id.date);
        }
    }
}
