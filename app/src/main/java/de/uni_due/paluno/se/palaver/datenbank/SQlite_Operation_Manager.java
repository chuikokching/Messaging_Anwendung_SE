package de.uni_due.paluno.se.palaver.datenbank;

import android.content.Context;

public class SQlite_Operation_Manager {

    private static SQliteManager operator;

    public static SQliteManager newInstance(Context context)
    {
        if(operator==null)
        {
            return new SQliteManager(context);
        }
        return operator;
    }

}
