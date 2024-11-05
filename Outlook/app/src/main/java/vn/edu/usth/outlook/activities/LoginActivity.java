package vn.edu.usth.outlook.activities;

import android.content.Intent;
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
        // Lấy thông tin nhập từ người dùng và loại bỏ khoảng trắng thừa
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Đặt lại trạng thái hiển thị của tất cả các thông báo lỗi
        resetLoginErrorMessages();

        // Kiểm tra nếu tên đăng nhập hoặc mật khẩu bị bỏ trống
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

        // Nếu có lỗi, không tiếp tục đăng nhập
        if (hasError) {
            return;
        }

        // Kiểm tra xem cơ sở dữ liệu có bất kỳ người dùng nào không
        if (!databaseHelper.hasUsers()) {
            Toast.makeText(this, "No accounts found. Please sign up.", Toast.LENGTH_SHORT).show();
            return; // Dừng lại nếu chưa có tài khoản
        }

        // Nếu cả tên đăng nhập và mật khẩu đều hợp lệ, tiến hành kiểm tra thông tin đăng nhập
        if (databaseHelper.checkUserCredentials(username, password)) {
            // Nếu thông tin khớp, cho phép đăng nhập và chuyển đến MainActivity
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else if (!databaseHelper.checkUserCredentials(username, password)) {
            Toast.makeText(this, "Sai tên đăng nhập hoặc mật khẩu. Vui lòng kiểm tra lại.", Toast.LENGTH_SHORT).show();
        } else if (!databaseHelper.hasUsers()) {
            Toast.makeText(this, "Hiện tại chưa có tài khoản nào. Vui lòng đăng ký.", Toast.LENGTH_SHORT).show();
        }
    }

    private void resetLoginErrorMessages() {
        // Đặt lại thông báo lỗi về trạng thái ban đầu và ẩn chúng
        notInputUsername.setText("Please input username");
        notInputUsername.setVisibility(View.GONE);

        notInputPassword.setText("Please input password");
        notInputPassword.setVisibility(View.GONE);
    }

}