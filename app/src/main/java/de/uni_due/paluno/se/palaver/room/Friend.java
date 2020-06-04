package de.uni_due.paluno.se.palaver.room;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "friend_list")
public class Friend {
    @PrimaryKey(autoGenerate=true)
    private int uid;

    @ColumnInfo(name = "nickname")
    private String nickname;

    public Friend(String nickname) {
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }
}
