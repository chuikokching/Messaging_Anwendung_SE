package de.uni_due.paluno.se.palaver.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Friend.class, Chat.class}, version = 1)
public abstract class PalaverDatabase extends RoomDatabase {
    public static final String DataBaseNAME = "Palaver.db";
    private static volatile PalaverDatabase instance;

    public static synchronized PalaverDatabase getInstance(Context context) {
        if (instance == null) {
            instance = create(context);
        }
        return instance;
    }

    private static PalaverDatabase create(final Context context) {
        return Room.databaseBuilder(context,
                PalaverDatabase.class, DataBaseNAME).allowMainThreadQueries().build();
    }

    public abstract FriendDao getFriendDao();
    public abstract ChatDao getChatDao();
}
