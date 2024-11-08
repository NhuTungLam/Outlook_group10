package vn.edu.usth.outlook.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;


import vn.edu.usth.outlook.Email_receiver;
import vn.edu.usth.outlook.R;
import vn.edu.usth.outlook.activities.DetailMail;
import vn.edu.usth.outlook.adapter.ReceiveAdapter;
import vn.edu.usth.outlook.database.DatabaseHelper;
import vn.edu.usth.outlook.listener.SelectListener;


public class InboxFragment extends Fragment {


    private RecyclerView recyclerView;
    private ReceiveAdapter receiveAdapter;
    private Email_receiver deletedMail = null;
    private List<String> archivedMail = new ArrayList<>();
    private List<Email_receiver> emailReceiveList;
    private DatabaseHelper dbHelper;
    private View placeholder;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inbox, container, false);

        // Khởi tạo RecyclerView và placeholder
        recyclerView = view.findViewById(R.id.recycler_inbox);
        placeholder = view.findViewById(R.id.inbox_holder);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));

        dbHelper = new DatabaseHelper(getContext());

        // Lấy email người dùng hiện tại từ SharedPreferences
        SharedPreferences preferences = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String receiveEmail = preferences.getString("loggedInEmail", null);

        if (receiveEmail != null) {
            emailReceiveList = dbHelper.getReceiveEmails(receiveEmail); // Lấy danh sách email từ DB cho người dùng hiện tại
        } else {
            emailReceiveList = new ArrayList<>(); // Khởi tạo danh sách trống nếu không tìm thấy email
        }

        receiveAdapter = new ReceiveAdapter(getContext(), emailReceiveList, (SelectListener) this);
        recyclerView.setAdapter(receiveAdapter);

        // Hiển thị placeholder nếu danh sách rỗng
        togglePlaceholder();

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        return view;
    }

    private void togglePlaceholder() {
        if (emailReceiveList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            placeholder.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            placeholder.setVisibility(View.GONE);
        }
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
        deletedMail = emailReceiveList.get(position);
        emailReceiveList.remove(position);
        receiveAdapter.notifyItemRemoved(position);

        Snackbar.make(recyclerView, "Email deleted", Snackbar.LENGTH_LONG)
                .setAction("Undo", v -> undoDelete(position))
                .show();
    }

    private void undoDelete(int position) {
        if (deletedMail != null) {
            emailReceiveList.add(position, deletedMail);
            receiveAdapter.notifyItemInserted(position);
            deletedMail = null;
        }
    }

    private void handleArchive(int position) {
        final Email_receiver email = emailReceiveList.get(position);
        archivedMail.add(String.valueOf(email));
        emailReceiveList.remove(position);
        receiveAdapter.notifyItemRemoved(position);

        Snackbar.make(recyclerView, email + ", Archived.", Snackbar.LENGTH_LONG)
                .setAction("Undo", v -> undoArchive(email, position))
                .show();
    }

    private void undoArchive(Email_receiver email, int position) {
        archivedMail.remove(archivedMail.lastIndexOf(email));
        emailReceiveList.add(position, email);
        receiveAdapter.notifyItemInserted(position);
    }

    @Override
    public void onItemClicked(int position) {
        Intent intent = new Intent(getActivity(), DetailMail.class);
        startActivity(intent);
        Email_receiver email = emailReceiveList.get(position);
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


}