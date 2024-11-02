package vn.edu.usth.outlook.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import vn.edu.usth.outlook.R;
import vn.edu.usth.outlook.activities.LoginActivity;

public class SignupFragment extends Fragment {

    private EditText newEmail, phoneNumber, username, password, confirmPassword;
    private TextView newEmailError, phoneNumberError, usernameError, passwordError, confirmPasswordError, invalidEmailError,
    invalidPhoneNumber, invalidPassword, invalidConfirmPass;
    private MaterialButton signupButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup, container, false);

        // Initialize views
        newEmail = view.findViewById(R.id.newEmail);
        phoneNumber = view.findViewById(R.id.phoneNumber);
        username = view.findViewById(R.id.username);
        password = view.findViewById(R.id.password);
        confirmPassword = view.findViewById(R.id.confirmPassword);

        // Initialize error views
        newEmailError = view.findViewById(R.id.newEmailError);
        phoneNumberError = view.findViewById(R.id.phoneNumberError);
        usernameError = view.findViewById(R.id.usernameError);
        passwordError = view.findViewById(R.id.passwordError);
        confirmPasswordError = view.findViewById(R.id.confirmPasswordError);
        invalidEmailError = view.findViewById(R.id.invalidEmailError);
        invalidPhoneNumber = view.findViewById(R.id.invalidPhoneNumber);
        invalidPassword = view.findViewById(R.id.invalidPassword);

        signupButton = view.findViewById(R.id.signupButton);

        // Set click listener for signup button
        signupButton.setOnClickListener(v -> validateAndSignUp());

        return view;
    }

    // Method to validate fields and sign up
    private void validateAndSignUp() {
        boolean isValid = true;

        // Reset all error messages to GONE
        newEmailError.setVisibility(View.GONE);
        phoneNumberError.setVisibility(View.GONE);
        usernameError.setVisibility(View.GONE);
        passwordError.setVisibility(View.GONE);
        confirmPasswordError.setVisibility(View.GONE);
        invalidEmailError.setVisibility(View.GONE); // Reset thông báo email không hợp lệ
        invalidPhoneNumber.setVisibility(View.GONE);


        String emailInput = newEmail.getText().toString().trim();

        // check email
        if (emailInput.isEmpty()) {
            newEmailError.setVisibility(View.VISIBLE);
            isValid = false;
        } else if (!isValidEmail(emailInput)) {
            invalidEmailError.setVisibility(View.VISIBLE);  // Hiển thị thông báo email không hợp lệ
            isValid = false;
        }

        // check phone number
        String phoneInput = phoneNumber.getText().toString().trim();
        if (phoneNumber.getText().toString().trim().isEmpty()) {
            phoneNumberError.setVisibility(View.VISIBLE);
            isValid = false;
        } else if(!isValidPhone(phoneInput)){
            invalidPhoneNumber.setVisibility(View.VISIBLE);
        }

        // Check username
        if (username.getText().toString().trim().isEmpty()) {
            usernameError.setVisibility(View.VISIBLE);
            isValid = false;
        }

        // Check password
        String passwordInput = password.getText().toString().trim();
        if (password.getText().toString().trim().isEmpty()) {
            passwordError.setVisibility(View.VISIBLE);
            isValid = false;
        } else if (!isValidPassword(passwordInput)) {
            invalidPassword.setVisibility(View.VISIBLE); // Hiển thị thông báo mật khẩu không hợp lệ
            isValid = false;
        }

        // Check confirm password
        if (confirmPassword.getText().toString().trim().isEmpty() ||
                !confirmPassword.getText().toString().equals(password.getText().toString())) {
            confirmPasswordError.setVisibility(View.VISIBLE);
            isValid = false;
        }

        // if all case valid
        if (isValid) {
            Toast.makeText(getContext(), "Registration successful!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
        }
    }

    private boolean isValidEmail(String email) {
        // Check email by Regular Expression (Regex)
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        return email.matches(emailPattern);
    }

    private boolean isValidPhone(String phone) {
        String phonePattern = "^\\d{10,11}$"; // phone number only have 10 or 11 char
        return phone.matches(phonePattern);
    }

    private boolean isValidPassword(String password) {
        // At least 8 characters, uppercase first letter, contain at least 1 number and 1 special character
        String passwordPattern = "^(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
        return password.matches(passwordPattern);
    }
}
