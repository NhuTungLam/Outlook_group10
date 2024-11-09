package vn.edu.usth.outlook.activities;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
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


public class ForgotPasswordActivity extends AppCompatActivity {
    private DatabaseHelper databaseHelper;
    private EditText emailEditText, newPasswordEditText, confirmPasswordEditText;
    private TextView emailError, newPassError, confirmPassError;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        databaseHelper = new DatabaseHelper(this);

        emailEditText = findViewById(R.id.emailInput);
        newPasswordEditText = findViewById(R.id.newPasswordInput);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordInput);

        emailError = findViewById(R.id.input_your_email);
        newPassError = findViewById(R.id.input_new_pass);
        confirmPassError = findViewById(R.id.input_confirm_pass);

        // Enable Edge-to-edge mode (optional)
        EdgeToEdge.enable(this);  // Assuming EdgeToEdge is a class for enabling edge-to-edge mode

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();

        });

        MaterialButton resetButton = findViewById(R.id.resetButton);

        // Set click listener for signup button
        resetButton.setOnClickListener(v -> resetPassword());

        // Set padding based on system bars insets
        View forgotPassView = findViewById(R.id.forgotpass);  // Assuming R.id.forgotpass is the root view
        ViewCompat.setOnApplyWindowInsetsListener(forgotPassView, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void resetPassword() {
        Log.d("ForgotPasswordActivity", "resetPassword() called");
        String email = emailEditText.getText().toString().trim();
        String newPassword = newPasswordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        resetErrorMessages();

        boolean isValid = true;

        // Kiểm tra xem email có tồn tại không

        if (email.isEmpty()) {
            emailError.setVisibility(View.VISIBLE);
            isValid = false;
        } else if (!databaseHelper.checkEmailExists(email)) {
            Log.d("ForgotPasswordActivity", "Email not found");
            emailError.setText("Email not exist");
            emailError.setVisibility(View.VISIBLE);
            isValid = false;
        }

        // Kiểm tra xem mật khẩu mới có trùng với mật khẩu cũ không
        if (newPassword.isEmpty()) {
            newPassError.setVisibility(View.VISIBLE);
            isValid = false;
        } else if (databaseHelper.checkPasswordExist(email, newPassword)) {
            Log.d("ForgotPasswordActivity", "New password is invalid");
            newPassError.setText("Password already exist");
            newPassError.setVisibility(View.VISIBLE);
            isValid = false;
        }

        // Kiểm tra xem hai mật khẩu mới có khớp nhau không
        if (confirmPassword.isEmpty()) {
            confirmPassError.setVisibility(View.VISIBLE);
            isValid = false;
        } else if (!newPassword.equals(confirmPassword)) {
            Log.d("ForgotPasswordActivity", "Passwords do not match");
            confirmPassError.setText("confirm password must similar to new password");
            confirmPassError.setVisibility(View.VISIBLE);
            isValid = false;
        }

        // Cập nhật mật khẩu mới vào cơ sở dữ liệu
        if (isValid && databaseHelper.updatePassword(email, newPassword)) {
            // Cập nhật thành công, hiển thị thông báo
            Toast.makeText(this, "Mật khẩu đã được cập nhật thành công!", Toast.LENGTH_SHORT).show();
        } else {
            // Cập nhật thất bại, hiển thị thông báo lỗi chi tiết hơn
            if (!isValid) {
                // Hiển thị thông báo lỗi tổng quan về việc nhập liệu không hợp lệ
                Toast.makeText(this, "Vui lòng kiểm tra lại thông tin nhập liệu!", Toast.LENGTH_SHORT).show();
            } else {
                // Hiển thị thông báo lỗi cụ thể về việc cập nhật database thất bại
                Toast.makeText(this, "Đã xảy ra lỗi khi cập nhật mật khẩu vào cơ sở dữ liệu!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void resetErrorMessages() {
        emailError.setText("");
        emailError.setVisibility(View.GONE);
        newPassError.setText("");
        newPassError.setVisibility(View.GONE);
        confirmPassError.setText("");
        confirmPassError.setVisibility(View.GONE);
    }

    // Hàm cập nhật mật khẩu trong DatabaseHelper

}