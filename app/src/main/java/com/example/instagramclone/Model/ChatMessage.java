package com.example.instagramclone.Model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatMessage {
    private String body;
    private String sender;
    private String recipient;
    private String timeSent;
    private Boolean read;

    public String getMessageImage() {
        return messageImage;
    }

    public void setMessageImage(String messageImage) {
        this.messageImage = messageImage;
    }

    private String messageImage;

    public Boolean getRead() {
        return read;
    }

    public void setRead(Boolean read) {
        this.read = read;
    }


    public ChatMessage()
    { }
    public ChatMessage(String body, String sender, String recipient) {
        this.body = body;
        this.sender = sender;
        this.recipient = recipient;
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        timeSent = dateFormat.format(new Date().getTime());
        read = false;
        messageImage="";
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
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

    public String getTimeSent() {
        return timeSent;
    }

    public void setTimeSent(String timeSent) {
        this.timeSent = timeSent;
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "body='" + body + '\'' +
                ", sender='" + sender + '\'' +
                ", recipient='" + recipient + '\'' +
                ", read='"+read+'\''+
                '}';
    }
}
