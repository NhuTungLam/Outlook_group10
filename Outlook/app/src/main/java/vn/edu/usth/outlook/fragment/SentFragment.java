package vn.edu.usth.outlook.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.preference.PreferenceManager;
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
import vn.edu.usth.outlook.Email_Sent;
import vn.edu.usth.outlook.R;
import vn.edu.usth.outlook.activities.DetailMail;
import vn.edu.usth.outlook.database.DatabaseHelper;
import vn.edu.usth.outlook.listener.SelectListener;
import vn.edu.usth.outlook.adapter.SentAdapter;

public class SentFragment extends Fragment implements SelectListener {

    private RecyclerView recyclerView;
    private SentAdapter sentAdapter;
    private Email_Sent deletedMail = null;
    private List<String> archivedMail = new ArrayList<>();
    private List<Email_Sent> emailList;
    private DatabaseHelper dbHelper;
    private View placeholderLayout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sent, container, false);

        // Khởi tạo RecyclerView và placeholder
        recyclerView = view.findViewById(R.id.recycler_sent);
        placeholderLayout = view.findViewById(R.id.placeholder_layout);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));

        dbHelper = new DatabaseHelper(getContext());

        // Lấy email người dùng hiện tại từ SharedPreferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String loggedInEmail = preferences.getString("loggedInEmail", null);

        if (loggedInEmail != null) {
            // Lấy danh sách email đã gửi từ DB cho người dùng hiện tại
            emailList = dbHelper.getSentEmails(loggedInEmail);
        } else {
            emailList = new ArrayList<>(); // Khởi tạo danh sách trống nếu không tìm thấy email
        }

        // Thiết lập Adapter và RecyclerView
        sentAdapter = new SentAdapter(getContext(), emailList, this);
        recyclerView.setAdapter(sentAdapter);

        // Hiển thị placeholder nếu danh sách rỗng
        togglePlaceholder();

        // Thiết lập thao tác vuốt cho RecyclerView
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        return view;
    }

    private void togglePlaceholder() {
        if (emailList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            placeholderLayout.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            placeholderLayout.setVisibility(View.GONE);
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
        final Email_Sent email = emailList.get(position);
        archivedMail.add(String.valueOf(email));
        emailList.remove(position);
        sentAdapter.notifyItemRemoved(position);

        Snackbar.make(recyclerView, email + ", Archived.", Snackbar.LENGTH_LONG)
                .setAction("Undo", v -> undoArchive(email, position))
                .show();
    }

    private void undoArchive(Email_Sent email, int position) {
        archivedMail.remove(archivedMail.lastIndexOf(email));
        emailList.add(position, email);
        sentAdapter.notifyItemInserted(position);
    }

    @Override
    public void onItemClicked(int position) {
        Intent intent = new Intent(getContext(), DetailMail.class);
        Email_Sent email = emailList.get(position);
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
