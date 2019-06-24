package de.uni_due.paluno.se.palaver.Datenbank;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static android.app.PendingIntent.getActivity;

public class MysqliteHelper extends SQLiteOpenHelper {

    SharedPreferences speicher_fragment;

    SharedPreferences.Editor speicher_editor;



    public MysqliteHelper( Context context, String name,  SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        speicher_fragment = context.getSharedPreferences("loginUser", Context.MODE_PRIVATE);
        speicher_editor = speicher_fragment.edit();

    }

    public MysqliteHelper( Context context) {
        super(context, Constant.DATABASE_NAME, null, Constant.DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.i("tag","--------------OnCreate------------");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        Log.i("tag","--------------OnUpgrade------------");
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        Log.i("tag","--------------OnOpen------------");
    }
}
