package vn.edu.usth.outlook.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable; // Thêm Nullable
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
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

public class InboxFragment extends Fragment implements SelectListener {

    private static final int REQUEST_CODE_DETAIL_MAIL = 1;

    private RecyclerView recyclerInbox;
    private ReceiveAdapter receiveAdapter;
    private Email_receiver deletedMail = null;
    private List<String> archivedMail = new ArrayList<>();
    private List<Email_receiver> emailReceiveList;
    private DatabaseHelper dbHelper;
    private View inboxholder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inbox, container, false);

        // Khởi tạo RecyclerView và placeholder
        recyclerInbox = view.findViewById(R.id.recycler_inbox);
        inboxholder = view.findViewById(R.id.inbox_holder);
        recyclerInbox.setHasFixedSize(true);
        recyclerInbox.setLayoutManager(new GridLayoutManager(getContext(), 1));

        dbHelper = new DatabaseHelper(getContext());

        // Lấy email người dùng hiện tại từ SharedPreferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String loggedInEmail = preferences.getString("loggedInEmail", null);

        if (loggedInEmail != null) {
            emailReceiveList = dbHelper.getReceiveEmails(loggedInEmail); // Lấy danh sách email từ DB cho người dùng hiện tại
        } else {
            emailReceiveList = new ArrayList<>(); // Khởi tạo danh sách trống nếu không tìm thấy email
        }

        receiveAdapter = new ReceiveAdapter(getContext(), emailReceiveList, (SelectListener) this);
        recyclerInbox.setAdapter(receiveAdapter);

        // Hiển thị placeholder nếu danh sách rỗng
        togglePlaceholder();

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerInbox);

        return view;
    }

    private void togglePlaceholder() {
        if (emailReceiveList.isEmpty()) {
            recyclerInbox.setVisibility(View.GONE);
            inboxholder.setVisibility(View.VISIBLE);
        } else {
            recyclerInbox.setVisibility(View.VISIBLE);
            inboxholder.setVisibility(View.GONE);
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
        emailReceiveList.remove(position); // Xóa email khỏi danh sách hiển thị
        receiveAdapter.notifyItemRemoved(position); // Cập nhật RecyclerView để ẩn email

        Snackbar.make(recyclerInbox, "Email deleted", Snackbar.LENGTH_LONG)
                .setAction("Undo", v -> undoDelete(position))
                .show();
    }

    private void undoDelete(int position) {
        if (deletedMail != null) {
            // Thêm email lại vào danh sách hiển thị nếu "Undo" được bấm
            emailReceiveList.add(position, deletedMail);
            receiveAdapter.notifyItemInserted(position);
            deletedMail = null;
        }
    }

    private void handleArchive(int position) {
        final Email_receiver email = emailReceiveList.get(position);
        emailReceiveList.remove(position); // Xóa email khỏi danh sách hiển thị
        receiveAdapter.notifyItemRemoved(position); // Cập nhật RecyclerView để ẩn email

        Snackbar.make(recyclerInbox, "Email archived", Snackbar.LENGTH_LONG)
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
        Intent intent = new Intent(getContext(), DetailMail.class);
        Email_receiver email = emailReceiveList.get(position);
        // Truyền dữ liệu email vào intent
        intent.putExtra("email_id", email.getId());
        intent.putExtra("sender", email.getSender());
        intent.putExtra("receiver", email.getReceiver());
        intent.putExtra("subject", email.getSubject());
        intent.putExtra("Me", email.getSender());
        intent.putExtra("content", email.getContent());
        intent.putExtra("position", position);
        startActivityForResult(intent, REQUEST_CODE_DETAIL_MAIL);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_DETAIL_MAIL && resultCode == getActivity().RESULT_OK && data != null) {
            int emailId = data.getIntExtra("email_id", -1);

            // Xóa email khỏi danh sách hiển thị
            for (int i = 0; i < emailReceiveList.size(); i++) {
                if (emailReceiveList.get(i).getId() == emailId) {
                    emailReceiveList.remove(i);
                    receiveAdapter.notifyItemRemoved(i);
                    break;
                }
            }
        }
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

    }
}
