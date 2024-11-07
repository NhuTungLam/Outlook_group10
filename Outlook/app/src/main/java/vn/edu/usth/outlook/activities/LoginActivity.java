package vn.edu.usth.outlook.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

    // Method to handle login logic
    private void handleLogin() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        resetLoginErrorMessages();

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

        if (hasError) {
            return;
        }

        if (!databaseHelper.hasUsers()) {
            Toast.makeText(this, "No accounts found. Please sign up.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (databaseHelper.checkUserCredentials(username, password)) {
            // Lấy email của người dùng từ cơ sở dữ liệu
            String userEmail = databaseHelper.getUserEmailByUsername(username);

            // Lưu email vào SharedPreferences
            SharedPreferences preferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("loggedInEmail", userEmail); // lưu email của tài khoản hiện tại
            editor.apply();

            // Chuyển đến MainActivity sau khi đăng nhập thành công
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Sai tên đăng nhập hoặc mật khẩu. Vui lòng kiểm tra lại.", Toast.LENGTH_SHORT).show();
        }
    }

    private void resetLoginErrorMessages() {
        notInputUsername.setVisibility(View.GONE);
        notInputPassword.setVisibility(View.GONE);
    }
}
