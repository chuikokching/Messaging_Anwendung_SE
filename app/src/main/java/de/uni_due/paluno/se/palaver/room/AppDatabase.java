package de.uni_due.paluno.se.palaver.room;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import de.uni_due.paluno.se.palaver.room.Friend;
import de.uni_due.paluno.se.palaver.room.FriendDao;

@Database(entities = {Friend.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract FriendDao friendDao();
}
