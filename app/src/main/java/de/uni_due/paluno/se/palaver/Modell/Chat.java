package de.uni_due.paluno.se.palaver.Modell;

public class Chat {

    private String sender;
    private String receiver;
    private String message;

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public void setMessage(String message) {
        this.message = message;
    }



    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getMessage() {
        return message;
    }


    public Chat(String s, String r,String m)
    {
        this.message=m;
        this.sender=s;
        this.receiver=r;
    }

    public Chat(){

    }


}
