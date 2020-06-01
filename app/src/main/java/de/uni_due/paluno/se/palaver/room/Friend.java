package de.uni_due.paluno.se.palaver.room;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "user_friendlist")
public class Friend {
    @PrimaryKey
    public int uid;

    @ColumnInfo(name = "nickName")
    private String nickName;

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
}
