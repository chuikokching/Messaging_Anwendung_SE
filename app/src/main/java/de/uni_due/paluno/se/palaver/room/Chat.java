package de.uni_due.paluno.se.palaver.room;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "chat_info")
public class Chat {
    @PrimaryKey(autoGenerate=true)
    private int cid;

    @ColumnInfo(name = "friend")
    private String friend;

    @ColumnInfo(name = "mimetype")
    private String mimeType;

    @ColumnInfo(name = "data")
    private String data;

    @ColumnInfo(name = "datetime")
    private String dateTime;

    @ColumnInfo(name = "sendtype")
    private String sendType;

    public Chat(String friend, String mimeType, String data, String dateTime, String sendType) {
        this.friend = friend;
        this.mimeType = mimeType;
        this.data = data;
        this.dateTime = dateTime;
        this.sendType = sendType;
    }

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public String getFriend() {
        return friend;
    }

    public void setFriend(String friend) {
        this.friend = friend;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getSendType() {
        return sendType;
    }

    public void setSendType(String sendType) {
        this.sendType = sendType;
    }
}
