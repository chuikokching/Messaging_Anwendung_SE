package de.uni_due.paluno.se.palaver.room;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "friend_list")
public class Friend {
    @PrimaryKey(autoGenerate=true)
    private int uid;

    @ColumnInfo(name = "nickName")
    private String nickName;

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }
}
