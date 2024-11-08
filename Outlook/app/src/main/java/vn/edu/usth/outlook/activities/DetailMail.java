package vn.edu.usth.outlook.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

import vn.edu.usth.outlook.Email_receiver;
import vn.edu.usth.outlook.R;
import vn.edu.usth.outlook.database.DatabaseHelper;
import vn.edu.usth.outlook.listener.OnSwipeTouchListener;

public class DetailMail extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

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

        // Change status bar background
        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.background_all));
        setContentView(R.layout.activity_detail_mail);

        dbHelper = new DatabaseHelper(this); // Initialize the database helper

        // Nhận thông tin email từ intent khi mở từ SentFragment
        Intent intent = getIntent();
        if (intent != null) {
            emailId = intent.getIntExtra("email_id", 1); // Lấy ID của email để dễ quản lý
            senderEmail = intent.getStringExtra("sender");
            receiverEmail = intent.getStringExtra("receiver");
            emailSubject = intent.getStringExtra("subject");
            emailContent = intent.getStringExtra("content");
            emailTimestamp = intent.getStringExtra("timestamp");
        }

        if (emailId == -1) {
            Toast.makeText(this, "Email ID missing", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Ánh xạ các `TextView` trong giao diện XML
        TextView senderGmailTextView = findViewById(R.id.SenderGmailHere);
        TextView subjectTextView = findViewById(R.id.Send_head_email_here);
        TextView contentTextView = findViewById(R.id.Send_content_here);
        TextView timestampTextView = findViewById(R.id.D_time);

        // Cập nhật giao diện với dữ liệu email
        senderGmailTextView.setText(senderEmail);
        subjectTextView.setText(emailSubject);
        contentTextView.setText(emailContent);
        timestampTextView.setText(emailTimestamp);
        ImageButton backButton = findViewById(R.id.back_icon);
        backButton.setOnClickListener(v -> finish());

        Button replyIcon = findViewById(R.id.reply_button);
        replyIcon.setOnClickListener(v -> {
            Intent intentsent = new Intent(DetailMail.this, ComposeActivity.class);
            startActivity(intentsent);
            finish();
        });
    }


    public void morePopup(View view) {
        PopupMenu popup = new PopupMenu(this, view);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.options_head);
        popup.show();
    }

    public void morevertPopup(View view) {
        PopupMenu popup = new PopupMenu(this, view);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.options_more_vert);
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHelper.close();
    }
}
