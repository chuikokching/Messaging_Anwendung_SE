package de.uni_due.paluno.se.palaver;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class FirstActivity extends AppCompatActivity {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        // prüfen, ob schon eingeloggt ist
        SharedPreferences loginUser_SP = getSharedPreferences("loginUser", Context.MODE_PRIVATE);
        boolean isLogin = loginUser_SP.getBoolean("login", false);

        if(isLogin == false) {
            Intent splashScreenIntent = new Intent(this, Splash_screen.class);
            startActivity(splashScreenIntent);
            finish();
        } else {
            Intent userInterfaceActivityIntent = new Intent(this, User_Interface_Activity.class);
            startActivity(userInterfaceActivityIntent);
            finish();
        }
    }
}
