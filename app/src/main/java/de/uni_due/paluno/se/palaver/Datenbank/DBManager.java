package de.uni_due.paluno.se.palaver.Datenbank;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class DBManager {

    private static MysqliteHelper helper;
    public static MysqliteHelper getInstance(Context context){
        if(helper==null)
        {
            helper = new MysqliteHelper(context);
        }
        return helper;
    }

    public static void execute_SQL(SQLiteDatabase db,String sql)
    {
        if(db!=null)
        {
            if(sql!=null&&!"".equals(sql))
            {
                db.execSQL(sql);
            }
        }
    }
}
