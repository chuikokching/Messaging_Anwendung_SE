package de.uni_due.paluno.se.palaver;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Chat_Activity extends AppCompatActivity {
    TextView friendNameTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        String friendName = getIntent().getStringExtra("friendName");
        friendNameTextView = findViewById(R.id.friendName);
        friendNameTextView.setText(friendName);
    }
}
