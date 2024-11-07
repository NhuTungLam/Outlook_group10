package vn.edu.usth.outlook.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
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

    private SQLiteDatabase db;
    private EditText receiverEditText, subjectEditText, contentEditText;
    private String currentUserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Change status bar background color
        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.background_all));
        setContentView(R.layout.activity_compose);

        // Initialize the database
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        db = dbHelper.getWritableDatabase();

        // Retrieve the logged-in user's email from SharedPreferences
        SharedPreferences preferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        currentUserEmail = preferences.getString("loggedInEmail", null); // Default to null if not found

        // Check if the email is retrieved successfully
        if (currentUserEmail == null) {
            Toast.makeText(this, "Error: User not logged in.", Toast.LENGTH_SHORT).show();
            finish(); // End the activity if email is not available
            return;
        }

        // Initialize input fields
        receiverEditText = findViewById(R.id.receiverEditText);
        subjectEditText = findViewById(R.id.subjectEditText);
        contentEditText = findViewById(R.id.contentEditText);

        // Initialize email sender TextView
        TextView emailSenderTextView = findViewById(R.id.emailSender);  // Initialize your TextView
        emailSenderTextView.setText(currentUserEmail);  // Set the current user's email to the TextView

        ImageButton btnSend = findViewById(R.id.btnSend);
        ImageButton backBtn = findViewById(R.id.backBtn);

        // Send button click listener
        btnSend.setOnClickListener(v -> {
            String sender = currentUserEmail;  // Retrieve the current logged-in email
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
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Clears all other activities
                    startActivity(intent);

                } else {
                    Toast.makeText(this, "Failed to send email", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(this, "Error sending email", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        // Handle popup menu item clicks here
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null && db.isOpen()) {
            db.close();  // Close database when activity is destroyed
        }
    }
}
