package de.uni_due.paluno.se.palaver.room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface FriendDao {
    @Query("SELECT * FROM friend_list ORDER BY nickname ASC")
    List<Friend> getFriendList();

    @Query("SELECT * FROM friend_list WHERE nickname LIKE :name")
    Friend getFriend(String name);

    @Query("DELETE FROM friend_list")
    void deleteFriendTable();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addFriend(Friend friend);
}
