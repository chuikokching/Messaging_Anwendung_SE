package de.uni_due.paluno.se.palaver;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import de.uni_due.paluno.se.palaver.fragment.Fragment_chat;

public class Chat_Activity extends AppCompatActivity {
    private FragmentManager supportFragmentManager;
    private FragmentTransaction fragmentTransaction;
    private Fragment_chat chatFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        String friendName = getIntent().getStringExtra("friendName");

        supportFragmentManager = getSupportFragmentManager();
        fragmentTransaction = supportFragmentManager.beginTransaction();
        chatFragment = new Fragment_chat();
        Bundle arg = new Bundle();
        arg.putString("name_of_friend", friendName);
        chatFragment.setArguments(arg);
        fragmentTransaction.add(R.id.chat_fragment_container, chatFragment).commit();
    }
}
