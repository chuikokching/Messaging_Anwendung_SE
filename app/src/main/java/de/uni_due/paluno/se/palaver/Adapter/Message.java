package de.uni_due.paluno.se.palaver.Adapter;

public class Message {
    private String sender,recipient,data,mimetype;

    public Message(String sender,String recipient,String data,String mimetype){
        this.sender=sender;
        this.recipient=recipient;
        this.data=data;
        this.mimetype=mimetype;
    }

    public Message()
    {

    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getMimetype() {
        return mimetype;
    }

    public void setMimetype(String mimetype) {
        this.mimetype = mimetype;
    }
}
