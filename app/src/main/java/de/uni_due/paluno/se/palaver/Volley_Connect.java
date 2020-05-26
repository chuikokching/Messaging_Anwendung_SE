package de.uni_due.paluno.se.palaver;



import android.app.Application;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class Volley_Connect extends Application {
    public static RequestQueue queues;

    @Override
    public void onCreate() {
        super.onCreate();
        queues=Volley.newRequestQueue(getApplicationContext());
    }
    public static RequestQueue getVolleyQueues()
    {
        return queues;
    }
}
