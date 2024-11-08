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
import vn.edu.usth.outlook.activities.DetailMailSentActivity;
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

        recyclerView = view.findViewById(R.id.recycler_sent);
        placeholderLayout = view.findViewById(R.id.placeholder_layout);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));

        dbHelper = new DatabaseHelper(getContext());

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String loggedInEmail = preferences.getString("loggedInEmail", null);

        if (loggedInEmail != null) {
            emailList = dbHelper.getSentEmails(loggedInEmail);
        } else {
            emailList = new ArrayList<>();
        }

        sentAdapter = new SentAdapter(getContext(), emailList, this);
        recyclerView.setAdapter(sentAdapter);

        togglePlaceholder();

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


        // Đánh dấu email là đã xóa trong cơ sở dữ liệu
//        dbHelper.markEmailAsDeleted(deletedMail.getId());

        emailList.remove(position);
        sentAdapter.notifyItemRemoved(position);

        // Làm mới DeletedFragment
//        DeletedFragment deletedFragment = (DeletedFragment) getParentFragmentManager().findFragmentByTag("DeletedFragmentTag");
//        if (deletedFragment != null) {
//            deletedFragment.refreshDeletedEmails();
//        }


        Snackbar.make(recyclerView, "Email deleted", Snackbar.LENGTH_LONG)
                .setAction("Undo", v -> undoDelete(position))
                .show();


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
        Email_Sent email = emailList.get(position);
        Intent intent = new Intent(getContext(), DetailMailSentActivity.class);

        // Truyền dữ liệu email vào intent
        intent.putExtra("sender", email.getSender());
        intent.putExtra("receiver", email.getReceiver());
        intent.putExtra("subject", email.getSubject());
        intent.putExtra("content", email.getContent());

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
        // Xử lý nếu cần
    }
}
