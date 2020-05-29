package de.uni_due.paluno.se.palaver;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

public class firstActivity extends AppCompatActivity {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        // prüfen, ob schon eingeloggt ist
        SharedPreferences speicher = getSharedPreferences("loginUser", Context.MODE_PRIVATE);
        boolean isLogin = speicher.getBoolean("login", false);

        if(isLogin == false) {
            Intent intent = new Intent(this, Splash_screen.class);
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(this, User_Interface_Activity.class);
            startActivity(intent);
            finish();
        }
    }
}
