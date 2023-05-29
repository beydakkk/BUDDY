package com.example.findmate;

public class MatchRequest {

    String senderId, receiverId, status;

    public MatchRequest() {
    }

    public MatchRequest(String senderID, String receiverID, String status) {
        this.senderId = senderID;
        this.receiverId = receiverID;
        this.status = status;
    }

    public String getSenderID() {
        return senderId;
    }

    public void setSenderID(String senderID) {
        this.senderId = senderID;
    }

    public String getReceiverID() {
        return receiverId;
    }

    public void setReceiverID(String receiverID) {
        this.receiverId = receiverID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
