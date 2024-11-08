package vn.edu.usth.outlook.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;

import vn.edu.usth.outlook.Email_Sent;
import vn.edu.usth.outlook.Email_receiver;
import vn.edu.usth.outlook.R;
import vn.edu.usth.outlook.activities.DetailMail;
import vn.edu.usth.outlook.activities.DetailMailSentActivity;
import vn.edu.usth.outlook.adapter.DeletedAdapter;
import vn.edu.usth.outlook.database.DatabaseHelper;

public class DeletedFragment extends Fragment {

    private RecyclerView recyclerDeleted;
    private DeletedAdapter deletedAdapter;
    private List<Email_receiver> deletedInboxEmails;
    private List<Email_Sent> deletedSentEmails;
    private DatabaseHelper dbHelper;
    private View placeholderLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_deleted, container, false);

        // Khởi tạo RecyclerView và Layout quản lý danh sách email đã xóa
        recyclerDeleted = view.findViewById(R.id.recycler_deleted);
        placeholderLayout = view.findViewById(R.id.deleted_placeholder_layout); // Placeholder layout nếu danh sách rỗng
        recyclerDeleted.setHasFixedSize(true);
        recyclerDeleted.setLayoutManager(new GridLayoutManager(getContext(), 1));

        // Khởi tạo DatabaseHelper
        dbHelper = new DatabaseHelper(getContext());

        // Lấy email người dùng hiện tại từ SharedPreferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String currentUser = preferences.getString("loggedInEmail", null);

        // Lấy danh sách email đã xóa từ Inbox và Sent
        if (currentUser != null) {
            deletedInboxEmails = dbHelper.getDeletedInboxEmails(currentUser);
            deletedSentEmails = dbHelper.getDeletedSentEmails(currentUser);
        } else {
            deletedInboxEmails = new ArrayList<>();
            deletedSentEmails = new ArrayList<>();
        }

        // Kết hợp email đã xóa từ Inbox và Sent vào một danh sách chung
        List<Object> allDeletedEmails = new ArrayList<>();
        allDeletedEmails.addAll(deletedInboxEmails);
        allDeletedEmails.addAll(deletedSentEmails);

        // Thiết lập adapter cho RecyclerView
        deletedAdapter = new DeletedAdapter(getContext(), allDeletedEmails, position -> {
            Object email = allDeletedEmails.get(position);
            Intent intent;

            if (email instanceof Email_receiver) {
                // Nếu email là loại nhận (inbox)
                Email_receiver emailReceiver = (Email_receiver) email;
                intent = new Intent(getContext(), DetailMail.class);
                intent.putExtra("email_id", emailReceiver.getId());
                intent.putExtra("sender", emailReceiver.getSender());
                intent.putExtra("receiver", emailReceiver.getReceiver());
                intent.putExtra("subject", emailReceiver.getSubject());
                intent.putExtra("content", emailReceiver.getContent());
            } else {
                // Nếu email là loại gửi đi (sent)
                Email_Sent emailSent = (Email_Sent) email;
                intent = new Intent(getContext(), DetailMailSentActivity.class);
                intent.putExtra("email_id", emailSent.getId());
                intent.putExtra("sender", emailSent.getSender());
                intent.putExtra("receiver", emailSent.getReceiver());
                intent.putExtra("subject", emailSent.getSubject());
                intent.putExtra("content", emailSent.getContent());
            }

            startActivity(intent);
        });
        recyclerDeleted.setAdapter(deletedAdapter);

        // Kiểm tra xem có hiển thị placeholder nếu danh sách rỗng
        togglePlaceholder(allDeletedEmails);

        return view;
    }

    private void togglePlaceholder(List<Object> allDeletedEmails) {
        if (allDeletedEmails.isEmpty()) {
            recyclerDeleted.setVisibility(View.GONE);
            placeholderLayout.setVisibility(View.VISIBLE);
        } else {
            recyclerDeleted.setVisibility(View.VISIBLE);
            placeholderLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}
