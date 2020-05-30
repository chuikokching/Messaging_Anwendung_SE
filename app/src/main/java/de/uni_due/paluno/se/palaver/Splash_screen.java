package de.uni_due.paluno.se.palaver;

import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class Splash_screen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent mainActivityIntent = new Intent(getApplication(), MainActivity.class);
                startActivity(mainActivityIntent);
                finish();
            }
        }, 3000);
    }
}
