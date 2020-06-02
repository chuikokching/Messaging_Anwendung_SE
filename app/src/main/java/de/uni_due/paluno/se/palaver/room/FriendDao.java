package de.uni_due.paluno.se.palaver.room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface FriendDao {
    @Query("SELECT * FROM friend_list")
    List<Friend> getFriendList();

    @Query("SELECT * FROM friend_list WHERE nickName LIKE :name")
    Friend getFriend(String name);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addFriends(Friend... friends);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addFriend(Friend friend);
}
