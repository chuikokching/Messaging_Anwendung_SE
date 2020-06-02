package de.uni_due.paluno.se.palaver.room;

public enum SendTypeEnum {
    TYPE_SEND("send"),
    TYPE_RECEIVE("receive"),
    ;

    private String sendType;

    private SendTypeEnum(String sendType) {
        this.sendType = sendType;
    }

    public String getSendType() {
        return sendType;
    }

    public void setSendType(String sendType) {
        this.sendType = sendType;
    }
}
