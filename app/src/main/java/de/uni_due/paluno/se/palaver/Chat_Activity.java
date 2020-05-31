package de.uni_due.paluno.se.palaver;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class Chat_Activity extends AppCompatActivity {
    private static Boolean dualPane;
    private static final String EXTRA_CONTACT_NAME = "de.uni_due.paluno.se.palaver.contact_name";

    public static Intent newIntent(Context context, String contactName, Boolean isDualPane) {
        Intent intent = new Intent(context, Chat_Activity.class);
        intent.putExtra(EXTRA_CONTACT_NAME, contactName);
        dualPane = isDualPane;
        return intent;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        String contactName = getIntent().getStringExtra(EXTRA_CONTACT_NAME);
        getSupportActionBar().setTitle(contactName);
    }
}
