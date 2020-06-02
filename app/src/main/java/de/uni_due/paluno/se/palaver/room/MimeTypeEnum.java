package de.uni_due.paluno.se.palaver.room;

public enum MimeTypeEnum {
    TEXT_PLAIN("text/plain"),
    TEXT_COOR("text/coor"),
    IMAGE_PIC("image/pic");

    private String mimeType;

    private MimeTypeEnum(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getMimeType() {
        return this.mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }
}
