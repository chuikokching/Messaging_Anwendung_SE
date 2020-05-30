package de.uni_due.paluno.se.palaver.Datenbank;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class SQliteManager extends SQLiteOpenHelper
{
    /**
     *  constructor
     * @param context
     * @param name name of db
     * @param factory
     * @param version >=1
     */
    public SQliteManager(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public SQliteManager(Context context) {
        super(context, SQlite_Version_Manager.DATABASE_NAME, null, SQlite_Version_Manager.DATABASE_VERSION);
    }

    /**
     * invoke, wenn db is created.
     * @param sqLiteDatabase
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    /**
     * invoke, wenn db is updated.
     * @param sqLiteDatabase
     * @param i oldversion
     * @param i1 newversion
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    /**
     * invoke, wenn db is opened.
     * @param db
     */
    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }
}
