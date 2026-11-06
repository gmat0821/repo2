package com.example.clonewhatsapp.models;

public class Message {
    private String id;
    private String conversationId;
    private String senderId;
    private String content;
    private String createdAt;
    private String status; // sent, delivered, read

    public Message(String id, String conversationId, String senderId, String content, String createdAt, String status) {
        this.id = id;
        this.conversationId = conversationId;
        this.senderId = senderId;
        this.content = content;
        this.createdAt = createdAt;
        this.status = status;
    }

    public String getId() { return id; }
    public String getConversationId() { return conversationId; }
    public String getSenderId() { return senderId; }
    public String getContent() { return content; }
    public String getCreatedAt() { return createdAt; }
    public String getStatus() { return status; }
}