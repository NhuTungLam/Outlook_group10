package vn.edu.usth.outlook.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import vn.edu.usth.outlook.R;
import vn.edu.usth.outlook.database.DatabaseHelper;

public class DetailMail extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private int emailId;
    private String senderEmail;
    private String receiverEmail;
    private String emailSubject;
    private String emailContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Đặt màu nền cho status bar
        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.background_all));
        setContentView(R.layout.activity_detail_mail);

        // Khởi tạo dbHelper
        dbHelper = new DatabaseHelper(this);

        // Nhận dữ liệu từ Intent
        Intent intent = getIntent();
        if (intent != null) {
            emailId = intent.getIntExtra("email_id", -1);
            senderEmail = intent.getStringExtra("sender");
            receiverEmail = intent.getStringExtra("receiver");
            emailSubject = intent.getStringExtra("subject");
            emailContent = intent.getStringExtra("content");
        }

        // Kiểm tra emailId hợp lệ
        if (emailId == -1) {
            Toast.makeText(this, "Email ID missing", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Hiển thị thông tin email
        TextView senderGmailTextView = findViewById(R.id.SenderGmailHere);
        TextView receiverGmailTextView = findViewById(R.id.ReceiverGmailHere);
        TextView subjectTextView = findViewById(R.id.Send_head_email_here);
        TextView contentTextView = findViewById(R.id.Send_content_here);

        senderGmailTextView.setText(senderEmail);
        receiverGmailTextView.setText(receiverEmail);
        subjectTextView.setText(emailSubject);
        contentTextView.setText(emailContent);

        // Nút back
        ImageButton backButton = findViewById(R.id.back_icon);
        backButton.setOnClickListener(v -> finish());

        // Nút trả lời
        Button replyIcon = findViewById(R.id.reply_button);
        replyIcon.setOnClickListener(v -> {
            Intent replyIntent = new Intent(DetailMail.this, ComposeActivity.class);
            replyIntent.putExtra("receiver", senderEmail);  // Cài đặt người nhận là người gửi email ban đầu
            startActivity(replyIntent);
        });

        // Nút xóa email
        ImageButton deleteButton = findViewById(R.id.delete);
        deleteButton.setOnClickListener(v -> {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("email_id", emailId); // Truyền email ID để Fragment biết cần xóa email nào
            setResult(RESULT_OK, resultIntent);
            finish();
            Toast.makeText(this, "Email marked as deleted", Toast.LENGTH_SHORT).show();
        });

// Nút lưu trữ email
        ImageButton archiveButton = findViewById(R.id.archive_button);
        archiveButton.setOnClickListener(v -> {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("email_id", emailId); // Truyền email ID để Fragment biết cần lưu trữ email nào
            setResult(RESULT_OK, resultIntent);
            finish();
            Toast.makeText(this, "Email archived", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}
