package vn.edu.usth.outlook.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.material.snackbar.Snackbar;
import java.util.ArrayList;
import java.util.List;
import vn.edu.usth.outlook.Email_Sender;
import vn.edu.usth.outlook.R;
import vn.edu.usth.outlook.activities.DetailMail;
import vn.edu.usth.outlook.database.DatabaseHelper;
import vn.edu.usth.outlook.listener.SelectListener;
import vn.edu.usth.outlook.adapter.SentAdapter;

public class SentFragment extends Fragment implements SelectListener {

    private RecyclerView recyclerView;
    private SentAdapter sentAdapter;
    private Email_Sender deletedMail = null;
    private List<String> archivedMail = new ArrayList<>();
    private List<Email_Sender> emailList;
    private DatabaseHelper dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sent, container, false);

        recyclerView = view.findViewById(R.id.recycler_sent);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));

        dbHelper = new DatabaseHelper(getContext());

        // Láº¥y email ngÆ°á»i dÃ¹ng hiá»‡n táº¡i tá»« SharedPreferences
        SharedPreferences preferences = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String senderEmail = preferences.getString("loggedInEmail", null);

        if (senderEmail != null) {
            emailList = dbHelper.getSentEmails(senderEmail); // Láº¥y danh sÃ¡ch email tá»« DB cho ngÆ°á»i dÃ¹ng hiá»‡n táº¡i
        } else {
            emailList = new ArrayList<>(); // Khá»Ÿi táº¡o danh sÃ¡ch trá»‘ng náº¿u khÃ´ng tÃ¬m tháº¥y email
        }

        sentAdapter = new SentAdapter(getContext(), emailList, this);
        recyclerView.setAdapter(sentAdapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        // Check if there are messages to display
        if (sentAdapter.isEmpty()) {
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

    private final ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            switch (direction) {
                case ItemTouchHelper.LEFT:
                    handleDelete(position);
                    break;
                case ItemTouchHelper.RIGHT:
                    handleArchive(position);
                    break;
            }
        }
    };

    private void handleDelete(int position) {
        deletedMail = emailList.get(position);
        emailList.remove(position);
        sentAdapter.notifyItemRemoved(position);

        Snackbar.make(recyclerView, "Email deleted", Snackbar.LENGTH_LONG)
                .setAction("Undo", v -> undoDelete(position))
                .show();
    }

    private void undoDelete(int position) {
        if (deletedMail != null) {
            emailList.add(position, deletedMail);
            sentAdapter.notifyItemInserted(position);
            deletedMail = null;
        }
    }

    private void handleArchive(int position) {
        final Email_Sender email = emailList.get(position);
        archivedMail.add(String.valueOf(email));
        emailList.remove(position);
        sentAdapter.notifyItemRemoved(position);

        Snackbar.make(recyclerView, email + ", Archived.", Snackbar.LENGTH_LONG)
                .setAction("Undo", v -> undoArchive(email, position))
                .show();
    }

    private void undoArchive(Email_Sender email, int position) {
        archivedMail.remove(archivedMail.lastIndexOf(email));
        emailList.add(position, email);
        sentAdapter.notifyItemInserted(position);
    }

    @Override
    public void onItemClicked(int position) {
        Intent intent = new Intent(getContext(), DetailMail.class);
        Email_Sender email = emailList.get(position);
        intent.putExtra("Name", email.getReceiver());
        intent.putExtra("Head Mail", email.getSubject());
        intent.putExtra("Me", email.getSender());
        intent.putExtra("Content", email.getContent());
        intent.putExtra("position", position);
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }

    @Override
    public void onLongItemClick(int position) {
        // Optional: Handle long-click if needed
    }
}
