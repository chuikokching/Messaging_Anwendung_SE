package de.uni_due.paluno.se.palaver.Datenbank;

import android.content.Context;

public class DBManager {

    private static MysqliteHelper helper;
    public static MysqliteHelper getInstance(Context context){
        if(helper==null)
        {
            helper = new MysqliteHelper(context);
        }
        return helper;
    }
}
