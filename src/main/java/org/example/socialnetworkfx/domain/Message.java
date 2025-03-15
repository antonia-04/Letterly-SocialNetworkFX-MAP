package org.example.socialnetworkfx.domain;

import java.time.LocalDateTime;

public class Message {
    private Long id;
    private String text;
    private LocalDateTime timestamp;
    private Long senderId;
    private Long recipientId;
    private Message replyTo;

    // Constructor
    public Message(String text, LocalDateTime timestamp, Long senderId, Long recipientId) {
        this.text = text;
        this.timestamp = timestamp;
        this.senderId = senderId;
        this.recipientId = recipientId;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public Long getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(Long recipientId) {
        this.recipientId = recipientId;
    }

    public Message getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(Message replyTo) {
        this.replyTo = replyTo;
    }

    @Override
    public String toString() {
        return text + '\'' +  timestamp ;
    }
}