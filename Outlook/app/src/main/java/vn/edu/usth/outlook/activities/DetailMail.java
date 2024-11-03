package vn.edu.usth.outlook.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

import vn.edu.usth.outlook.Email_Sender;
import vn.edu.usth.outlook.R;
import vn.edu.usth.outlook.db.DatabaseHelper; // Import your database helper
import vn.edu.usth.outlook.listener.OnSwipeTouchListener;

public class DetailMail extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    private DatabaseHelper dbHelper;
    private List<Email_Sender> emailSenderList = new ArrayList<>();
    private String currentUserEmail = "receiver@example.com";  // Replace with logged-in user's email

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Change status bar background
        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.background_all));
        setContentView(R.layout.activity_detail_mail);

        dbHelper = new DatabaseHelper(this); // Initialize the database helper

        // Load emails for the current user
        emailSenderList = loadEmailData(currentUserEmail);

        int position = getIntent().getIntExtra("position", 0);
        final int[] currentIndex = {position};

        // Retrieve and display email data based on position
        TextView Dname = findViewById(R.id.D_name);
        TextView DheadMail = findViewById(R.id.D_head_email);
        TextView Dcontent = findViewById(R.id.D_content);
        TextView Dreceiver = findViewById(R.id.toW);

        // Set the data if emails are available
        if (!emailSenderList.isEmpty()) {
            Dname.setText(emailSenderList.get(currentIndex[0]).getSender());
            DheadMail.setText(emailSenderList.get(currentIndex[0]).getSubject());
            Dcontent.setText(emailSenderList.get(currentIndex[0]).getContent());
            Dreceiver.setText(emailSenderList.get(currentIndex[0]).getReceiver());
        } else {
            Toast.makeText(this, "No emails found", Toast.LENGTH_SHORT).show();
        }

        // Back button to main page
        ImageButton backIcon = findViewById(R.id.back_icon);
<<<<<<< Updated upstream
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to navigate to the previous activity or fragment
                Intent intent = new Intent(DetailMail.this, MainActivity.class);
                startActivity(intent);

                // Optionally, finish the current activity to remove it from the stack
                finish();
            }
=======
        backIcon.setOnClickListener(v -> {
            Intent intent = new Intent(DetailMail.this, MainActivity.class);
            startActivity(intent);
            finish();
>>>>>>> Stashed changes
        });

        // Additional buttons and swipe actions (unchanged)
        ImageButton popupButton2 = findViewById(R.id.more);
<<<<<<< Updated upstream
        popupButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                morePopup(view);
            }
        });

        //popup more_vert
=======
        popupButton2.setOnClickListener(this::morePopup);
>>>>>>> Stashed changes
        ImageButton popupButton3 = findViewById(R.id.more_vert);
        popupButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                morevertPopup(view);
            }
        });

<<<<<<< Updated upstream
        // swipe to left or right
        LinearLayout swappable = findViewById(R.id.detail1);
=======
        // Handle swipe gestures to navigate emails
        LinearLayout swappable = findViewById(R.id.detailmail);
>>>>>>> Stashed changes
        swappable.setOnTouchListener(new OnSwipeTouchListener(DetailMail.this) {

            public void onSwipeRight() {
                if (currentIndex[0] > 0) {
                    currentIndex[0]--;
                    updateEmailDisplay(currentIndex[0]);
                }
            }
            public void onSwipeLeft() {
                if (currentIndex[0] < emailSenderList.size() - 1) {
                    currentIndex[0]++;
                    updateEmailDisplay(currentIndex[0]);
                }
            }
        });
    }

    // Load email data from the database for the specified receiver
    private List<Email_Sender> loadEmailData(String receiver) {
        List<Email_Sender> emails = new ArrayList<>();
        Cursor cursor = dbHelper.getReadableDatabase().rawQuery("SELECT * FROM emails WHERE receiver = ?", new String[]{receiver});

        if (cursor.moveToFirst()) {
            do {
                // Safely get the index for each column
                int senderIndex = cursor.getColumnIndex("sender");
                int subjectIndex = cursor.getColumnIndex("subject");
                int contentIndex = cursor.getColumnIndex("content");

<<<<<<< Updated upstream
//        // Add email data to the list
        emailSenderList.add(new Email_Sender("John Doe", "Meeting Tomorrow", "Don't forget about the meeting tomorrow at 10 AM.","John Doe"));
        emailSenderList.add(new Email_Sender("Jane Smith", "Project Update", "Here is the latest update on the project.", "John Doe"));
        emailSenderList.add(new Email_Sender("hellobaby@gmail.com", "Hello World", "Go out tonight?",  "me"));
        emailSenderList.add(new Email_Sender("hellobaby@gmail.com", "CHECK ATTENDANCE LIST", "Find your name in the Excel file below", "me"));
        emailSenderList.add(new Email_Sender("hellobaby@gmail.com", "INTERNSHIP", "Urgent to find a company", "me"));
        emailSenderList.add(new Email_Sender("nam@gmail.com", "xin chào buoi sang", "cam on phan anh nhé hhehe", "me"));
        emailSenderList.add(new Email_Sender("nam@gmail.com", "Mon nay kho qua", "bon em da rat co gang", "me"));
=======
                // Ensure the indexes are valid before attempting to retrieve data
                String sender = senderIndex != -1 ? cursor.getString(senderIndex) : "Unknown Sender";
                String subject = subjectIndex != -1 ? cursor.getString(subjectIndex) : "No Subject";
                String content = contentIndex != -1 ? cursor.getString(contentIndex) : "No Content";
>>>>>>> Stashed changes

                emails.add(new Email_Sender(sender, content, subject, receiver));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return emails;
    }


    // Update the email display with current email data
    private void updateEmailDisplay(int index) {
        TextView Dname = findViewById(R.id.D_name);
        TextView DheadMail = findViewById(R.id.D_head_email);
        TextView Dcontent = findViewById(R.id.D_content);
        TextView Dreceiver = findViewById(R.id.toW);

        Email_Sender email = emailSenderList.get(index);
        Dname.setText(email.getSender());
        DheadMail.setText(email.getSubject());
        Dcontent.setText(email.getContent());
        Dreceiver.setText(email.getReceiver());
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
}
