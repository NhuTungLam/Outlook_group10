package vn.edu.usth.outlook.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import vn.edu.usth.outlook.R;
import vn.edu.usth.outlook.activities.MainActivity;

public class NotificationFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notification, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize the back icon ImageButton
        ImageButton backIcon = view.findViewById(R.id.back_icon);

        // Set the onClickListener for the back icon
        backIcon.setOnClickListener(v -> {
            // Create an Intent to navigate to the MainActivity
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);

            // Optionally, remove the fragment from the back stack
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
    }
}
