package vn.edu.usth.outlook.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import vn.edu.usth.outlook.R;
import vn.edu.usth.outlook.database.DatabaseHelper;

public class DetailMailSentActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {


    private DatabaseHelper dbHelper;
    private int emailId;
    private String senderEmail;
    private String receiverEmail;
    private String emailSubject;
    private String emailContent;
    private String emailTimestamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_mail_sent);

        dbHelper = new DatabaseHelper(this);

        // Nhận thông tin email từ intent khi mở từ SentFragment
        Intent intent = getIntent();
        if (intent != null) {
            emailId = intent.getIntExtra("email_id", -1); // Lấy ID của email để dễ quản lý
            senderEmail = intent.getStringExtra("sender");
            receiverEmail = intent.getStringExtra("receiver");
            emailSubject = intent.getStringExtra("subject");
            emailContent = intent.getStringExtra("content");
        }

        if (emailId == -1) {
            Toast.makeText(this, "Email ID missing", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Ánh xạ các `TextView` trong giao diện XML
        TextView senderTextView = findViewById(R.id.D_name);
        TextView receiverTextView = findViewById(R.id.toW);
        TextView subjectTextView = findViewById(R.id.D_head_email);
        TextView contentTextView = findViewById(R.id.D_content);
        TextView timestampTextView = findViewById(R.id.D_time);

        // Cập nhật giao diện với dữ liệu email
        senderTextView.setText(senderEmail);
        receiverTextView.setText(receiverEmail);
        subjectTextView.setText(emailSubject);
        contentTextView.setText(emailContent);
        timestampTextView.setText(emailTimestamp);

        // Nút trở về
        ImageButton backButton = findViewById(R.id.back_icon);
        backButton.setOnClickListener(v -> finish());

        Button replyIcon = findViewById(R.id.reply_button);
        replyIcon.setOnClickListener(v -> {
            Intent replyIntent = new Intent(DetailMailSentActivity.this, ComposeActivity.class);
            replyIntent.putExtra("receiver", senderEmail); // Cài đặt người nhận là người gửi email ban đầu
            startActivity(replyIntent);
        });

        // Nút xóa email (đặt is_deleted = 1)
        ImageButton deleteButton = findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(v -> {
            dbHelper.markEmailAsDeleted(emailId); // Đánh dấu email là đã xóa
            Toast.makeText(this, "Email marked as deleted", Toast.LENGTH_SHORT).show();

            // Gửi lại kết quả về SentFragment
            Intent resultIntent = new Intent();
            resultIntent.putExtra("email_id", emailId);
            setResult(RESULT_OK, resultIntent);

            finish(); // Đóng Activity
        });

        // Nút lưu trữ email (đặt is_archived = 1)
        ImageButton archiveButton = findViewById(R.id.archive_button);
        archiveButton.setOnClickListener(v -> {
            dbHelper.markEmailAsArchived(emailId); // Đánh dấu email là đã lưu trữ
            Toast.makeText(this, "Email archived", Toast.LENGTH_SHORT).show();
            finish();
        });


        ImageButton popupButton = findViewById(R.id.more);
        popupButton.setOnClickListener(view -> morePopup(view));

        //popup more_vert
        ImageButton popupButton2 = findViewById(R.id.more_vert);
        popupButton2.setOnClickListener(view -> morevertPopup(view));
    }

    // Method for the "more" popup menu
    public void morePopup(View view) {
        PopupMenu popup = new PopupMenu(this, view);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.options_head);
        popup.show();
    }

    // Method for the "more_vert" popup menu
    public void morevertPopup(View view) {
        PopupMenu popup = new PopupMenu(this, view);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.options_more_vert);
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.move_to_folder) {
            Toast.makeText(this,  "Move to Folder selected", Toast.LENGTH_SHORT).show();
            return true;
        } else if (itemId == R.id.report_junk) {
            Toast.makeText(this, "Reported as Junk", Toast.LENGTH_SHORT).show();
            return true;
        } else if (itemId == R.id.move_to_other) {
            Toast.makeText(this, "Reported as Junk", Toast.LENGTH_SHORT).show();
            return true;
        } else if (itemId == R.id.forward_as_attachment) {
            Toast.makeText(this, "Reply selected", Toast.LENGTH_SHORT).show();
            return true;
        } else if (itemId == R.id.ignore_conversation) {
            Toast.makeText(this, "Reported as Junk", Toast.LENGTH_SHORT).show();
            return true;
        } else if (itemId == R.id.mark_as_unread) {
            Toast.makeText(this, "Reply selected", Toast.LENGTH_SHORT).show();
            return true;
        } else if (itemId == R.id.frag) {
            Toast.makeText(this, "Reported as Junk", Toast.LENGTH_SHORT).show();
            return true;
        } else if (itemId == R.id.pin) {
            Toast.makeText(this, "Reply selected", Toast.LENGTH_SHORT).show();
            return true;
        } else if (itemId == R.id.snooze) {
            Toast.makeText(this, "Reported as Junk", Toast.LENGTH_SHORT).show();
            return true;
        } else if (itemId == R.id.forward) {
            Toast.makeText(this, "Reply selected", Toast.LENGTH_SHORT).show();
            return true;
        } else if (itemId == R.id.deleted) {
            Toast.makeText(this, "Reported as Junk", Toast.LENGTH_SHORT).show();
            return true;

        } else if (itemId == R.id.reply) {
            Toast.makeText(this, "Reply selected", Toast.LENGTH_SHORT).show();
            return true;

        } else {
            return false;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }

}
