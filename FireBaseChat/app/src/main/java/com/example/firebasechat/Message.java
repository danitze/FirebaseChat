package com.example.firebasechat;

import com.google.firebase.auth.FirebaseUser;

import java.util.Date;

public class Message {
    private String messageText;
    private User messageUser;
    private long messageTime;

    public Message(String messageText, User messageUser) {
        this.messageText = messageText;
        this.messageUser = messageUser;

        this.messageTime = new Date().getTime();
    }

    public Message() {
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public User getMessageUser() {
        return messageUser;
    }

    public void setMessageUser(User messageUser) {
        this.messageUser = messageUser;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }
}
