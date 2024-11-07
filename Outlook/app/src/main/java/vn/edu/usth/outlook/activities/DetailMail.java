package vn.edu.usth.outlook.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

import vn.edu.usth.outlook.Email_Sender;
import vn.edu.usth.outlook.listener.OnSwipeTouchListener;
import vn.edu.usth.outlook.R;

public class DetailMail extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    private List<Email_Sender> emailSenderList = new ArrayList<>();


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Change status bar background to the corresponding
        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.background_all));
        setContentView(R.layout.activity_detail_mail);


        emailSenderList=loadEmailData();

        int position = getIntent().getIntExtra("position", 0);
        final int[] currentIndex = {position};

//         Retrieve the email data based on the position
        String name = emailSenderList.get(position).getSender();
        String headMail = emailSenderList.get(position).getSubject();
        String content = emailSenderList.get(position).getContent();
        String receiver = emailSenderList.get(position).getReceiver();

        // Update the UI elements with the email data
        TextView Dname = findViewById(R.id.D_name);
        TextView DheadMail = findViewById(R.id.D_head_email);
        TextView Dcontent = findViewById(R.id.D_content);
        TextView Dreceiver = findViewById(R.id.toW);

        Dname.setText(name);
        DheadMail.setText(headMail);
        Dcontent.setText(content);
        Dreceiver.setText(receiver);


        //back to mainpage

        ImageButton backIcon = findViewById(R.id.back_icon);
        Button replyIcon = findViewById(R.id.reply_button);
        backIcon.setOnClickListener(v -> {
            // Create an Intent to navigate to the previous activity or fragment
            Intent intent = new Intent(DetailMail.this, MainActivity.class);
            startActivity(intent);

            // Optionally, finish the current activity to remove it from the stack
            finish();
        });
        replyIcon.setOnClickListener(v -> {
            Intent intent = new Intent(DetailMail.this, ComposeActivity.class);
            startActivity(intent);
            finish();
        });

        //popup more
        ImageButton popupButton2 = findViewById(R.id.more);
        popupButton2.setOnClickListener(this::morePopup);

        //popup more_vert
        ImageButton popupButton3 = findViewById(R.id.more_vert);
        popupButton3.setOnClickListener(this::morevertPopup);

        // swipe to left or right
        LinearLayout swappable = findViewById(R.id.detailmail);
        swappable.setOnTouchListener(new OnSwipeTouchListener(DetailMail.this) {
            public void onSwipeRight() {
                // Swipe right
                if (currentIndex[0] > 0) {
                    currentIndex[0]--;

                    Dname.setText(emailSenderList.get(currentIndex[0]).getSender());
                    DheadMail.setText(emailSenderList.get(currentIndex[0]).getSubject());
                    Dcontent.setText(emailSenderList.get(currentIndex[0]).getContent());
//              Dimage.setImageResource(emailList.get(currentIndex[0]).getImage());
                }
            }
            public void onSwipeLeft() {
                // Swipe left
                if (currentIndex[0] < emailSenderList.size() - 1) {
                    currentIndex[0]++;

                    Dname.setText(emailSenderList.get(currentIndex[0]).getSender());
                    DheadMail.setText(emailSenderList.get(currentIndex[0]).getSubject());
                    Dcontent.setText(emailSenderList.get(currentIndex[0]).getContent());
                }
            }
        });
    }

    private List<Email_Sender> loadEmailData() {


//        // Add email data to the list
        emailSenderList.add(new Email_Sender("user1@example.com", "Do sth", "Subject 1", "me"));
        emailSenderList.add(new Email_Sender("user2@example.com", "Make sth", "Subject 2", "Long"));
        emailSenderList.add(new Email_Sender("user3@example.com", "Play sth", "Subject 3", "me"));


        return emailSenderList;
    }
    public void morePopup(View view) {
        PopupMenu popup = new PopupMenu(this, view);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.options_head);
        popup.show();
    }


    public void morevertPopup(View view){
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
