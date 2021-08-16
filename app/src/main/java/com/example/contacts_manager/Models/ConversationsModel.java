package com.example.contacts_manager.Models;

public class ConversationsModel {
    String senderId, receiverId, lastMessage, time;

    public ConversationsModel(String senderId, String receiverId, String lastMessage, String time) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.lastMessage = lastMessage;
        this.time = time;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}