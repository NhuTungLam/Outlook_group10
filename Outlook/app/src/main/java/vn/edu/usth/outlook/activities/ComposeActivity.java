package vn.edu.usth.outlook.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import vn.edu.usth.outlook.R;
import vn.edu.usth.outlook.database.DatabaseHelper;

public class ComposeActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    private EditText receiverEditText, subjectEditText, contentEditText;
    private String currentUserEmail;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Change status bar background color
        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.background_all));
        setContentView(R.layout.activity_compose);

        // Initialize the database helper
        dbHelper = new DatabaseHelper(this);

        // Retrieve the logged-in user's email from SharedPreferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        currentUserEmail = preferences.getString("loggedInEmail", null); // Lấy email đã đăng nhập

        // Kiểm tra và xử lý nếu không tìm thấy email
        if (currentUserEmail == null) {
            Toast.makeText(this, "Error: User not logged in.", Toast.LENGTH_SHORT).show();
            finish(); // Đóng Activity nếu không có email
            return;
        }

        // Initialize input fields
        receiverEditText = findViewById(R.id.receiverEditText);
        subjectEditText = findViewById(R.id.subjectEditText);
        contentEditText = findViewById(R.id.contentEditText);

        // Set the current user's email to the TextView
        TextView emailSenderTextView = findViewById(R.id.emailSender);
        emailSenderTextView.setText(currentUserEmail);

        ImageButton btnSend = findViewById(R.id.btnSend);
        ImageButton btnBack = findViewById(R.id.btnBack);

        // Send button click listener
        btnSend.setOnClickListener(v -> {
            String sender = currentUserEmail;
            String receiver = receiverEditText.getText().toString().trim();
            String subject = subjectEditText.getText().toString().trim();
            String content = contentEditText.getText().toString().trim();

            if (receiver.isEmpty() || subject.isEmpty() || content.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                // Call sendEmail from DatabaseHelper to insert data
                boolean success = dbHelper.sendEmail(sender, receiver, subject, content);
                if (success) {
                    Toast.makeText(this, "Email sent and saved", Toast.LENGTH_SHORT).show();

                    // Navigate back to the main activity
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);

                } else {
                    Toast.makeText(this, "Failed to send email", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(this, "Error sending email", Toast.LENGTH_SHORT).show();
            }
        });

        // Back button click listener
        btnBack.setOnClickListener(v -> onBackPressed());
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        // Handle popup menu item clicks here
        return false;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish(); // Ensure the activity is closed
        super.onBackPressed(); // Call the parent method
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();  // Close database when activity is destroyed
        }
    }
}
