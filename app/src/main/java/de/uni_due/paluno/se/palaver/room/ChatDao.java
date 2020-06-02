package de.uni_due.paluno.se.palaver.room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ChatDao {
    @Query("SELECT * FROM chat_info WHERE friend LIKE :friend")
    List<Chat> getChatListByName(String friend);

    @Insert
    void addChat(Chat chat);
}
