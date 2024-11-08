package vn.edu.usth.outlook.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.preference.PreferenceManager;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;

import vn.edu.usth.outlook.R;
import vn.edu.usth.outlook.database.DatabaseHelper;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText, passwordEditText;
    private TextView notInputUsername, notInputPassword;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        databaseHelper = new DatabaseHelper(this);

        // Set padding for system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        usernameEditText = findViewById(R.id.usernameLogin);
        passwordEditText = findViewById(R.id.passwordLogin);
        MaterialButton loginButton = findViewById(R.id.loginButton);

        notInputUsername = findViewById(R.id.notInputUsername);
        notInputPassword = findViewById(R.id.notInputPassword);

        // Set login button click listener
        loginButton.setOnClickListener(view -> handleLogin());
    }

    // Handle signup redirection
    public void goToSignUp(View view) {
        Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
        startActivity(intent);
        finish(); // Optionally finish this activity
    }

    public void goToForgotPass(View view) {
        Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
        startActivity(intent);
        finish();
    }

    // Method to handle login logic
    private void handleLogin() {
        // Get user input and remove extra spaces
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Reset the display state of all error messages
        resetLoginErrorMessages();

        // Check if username or password is blank
        boolean hasError = false;

        if (username.isEmpty()) {
            notInputUsername.setText("Please input username");
            notInputUsername.setVisibility(View.VISIBLE);
            hasError = true;
        }
        if (password.isEmpty()) {
            notInputPassword.setText("Please input password");
            notInputPassword.setVisibility(View.VISIBLE);
            hasError = true;
        }

        // If there is an error, do not continue logging in
        if (hasError) {
            return;
        }

        // Check if the database has any users
        if (!databaseHelper.hasUsers()) {
            Toast.makeText(this, "No accounts found. Please sign up.", Toast.LENGTH_SHORT).show();
            return;
        }

        // If both the username and password are valid, proceed to check the login information.
        if (databaseHelper.checkUserCredentials(username, password)) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = preferences.edit();

            editor.putBoolean("isLoggedIn", true);
            // Lấy email của người dùng từ database và lưu vào SharedPreferences
            String email = databaseHelper.getUserEmail(username);
            editor.putString("loggedInEmail", email);
            editor.apply();

            // Chuyển hướng đến MainActivity
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Invalid username or password. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    private void resetLoginErrorMessages() {
        // Reset error messages to their original state and hide them
        notInputUsername.setText("");
        notInputUsername.setVisibility(View.GONE);

        notInputPassword.setText("");
        notInputPassword.setVisibility(View.GONE);
    }
}
