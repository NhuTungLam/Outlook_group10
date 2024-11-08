package vn.edu.usth.outlook.fragment;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import vn.edu.usth.outlook.R;


public class SettingsFragment extends Fragment {

    private TextView emailTextView, emailTextView1, usernameTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        emailTextView = view.findViewById(R.id.email_address);
        emailTextView1 = view.findViewById(R.id.email_user);
        usernameTextView = view.findViewById(R.id.text1); // Update the text view for username

        // Get logged-in user information from SharedPreferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        String email = preferences.getString("loggedInEmail", "");
        String username = preferences.getString("loggedInUsername", ""); // Retrieve username

        // Set email and username in the UI
        emailTextView.setText(email);
        emailTextView1.setText(email);
        usernameTextView.setText(username);

        return view;
    }
}