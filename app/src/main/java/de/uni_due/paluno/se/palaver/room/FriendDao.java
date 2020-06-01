package de.uni_due.paluno.se.palaver.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface FriendDao {
    @Query("SELECT * FROM user_friendlist")
    List<Friend> getAll();

    @Query("SELECT * FROM user_friendlist WHERE uid IN (:userIds)")
    List<Friend> loadAllByIds(int[] userIds);

    @Query("SELECT * FROM user_friendlist WHERE nickName LIKE :first")
    Friend findByName(String first);

    @Insert
    void insertAll(Friend... friends);

    @Delete
    void delete(Friend friend);
}
