package com.example.clonewhatsapp.models;

public class Conversation {
    private String id;
    private String otherUserId;
    private String otherUserName;
    private String otherUserAvatar;
    private String lastMessage;
    private String lastMessageTime;

    public Conversation(String id, String otherUserId, String otherUserName, String otherUserAvatar, String lastMessage, String lastMessageTime) {
        this.id = id;
        this.otherUserId = otherUserId;
        this.otherUserName = otherUserName;
        this.otherUserAvatar = otherUserAvatar;
        this.lastMessage = lastMessage;
        this.lastMessageTime = lastMessageTime;
    }

    public String getId() { return id; }
    public String getOtherUserId() { return otherUserId; }
    public String getOtherUserName() { return otherUserName; }
    public String getOtherUserAvatar() { return otherUserAvatar; }
    public String getLastMessage() { return lastMessage; }
    public String getLastMessageTime() { return lastMessageTime; }
}