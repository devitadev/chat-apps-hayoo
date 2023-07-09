package com.temp.chatapps_hayoo.models;

import java.util.Date;

public class ChatMessage {
    private String senderID, receiverID, message, dateTime;
    private Date dateObject;
    private String conversationID, conversationName, conversationImage, conversationEmail;
    private Double conversationLatitude, conversationLongitude;
    private boolean conversationReadStatus;

    public ChatMessage(String senderID, String receiverID, String message, String dateTime, Date dateObject, String conversationID, String conversationName, String conversationImage, String conversationEmail, Double conversationLatitude, Double conversationLongitude, boolean conversationReatStatus) {
        this.senderID = senderID;
        this.receiverID = receiverID;
        this.message = message;
        this.dateTime = dateTime;
        this.dateObject = dateObject;
        this.conversationID = conversationID;
        this.conversationName = conversationName;
        this.conversationImage = conversationImage;
        this.conversationEmail = conversationEmail;
        this.conversationLatitude = conversationLatitude;
        this.conversationLongitude = conversationLongitude;
        this.conversationReadStatus = conversationReadStatus;
    }

    public ChatMessage() {
    }

    public String getSenderID() { return senderID; }

    public void setSenderID(String senderID) { this.senderID = senderID; }

    public String getReceiverID() { return receiverID; }

    public void setReceiverID(String receiverID) { this.receiverID = receiverID; }

    public String getMessage() { return message; }

    public void setMessage(String message) { this.message = message; }

    public String getDateTime() { return dateTime; }

    public void setDateTime(String dateTime) { this.dateTime = dateTime; }

    public Date getDateObject() { return dateObject; }

    public void setDateObject(Date dateObject) { this.dateObject = dateObject; }

    public String getConversationID() { return conversationID; }

    public void setConversationID(String conversationID) { this.conversationID = conversationID; }

    public String getConversationName() { return conversationName; }

    public void setConversationName(String conversationName) { this.conversationName = conversationName; }

    public String getConversationImage() { return conversationImage; }

    public void setConversationImage(String conversationImage) { this.conversationImage = conversationImage; }

    public String getConversationEmail() { return conversationEmail; }

    public void setConversationEmail(String conversationEmail) { this.conversationEmail = conversationEmail; }

    public Double getConversationLatitude() { return conversationLatitude; }

    public void setConversationLatitude(Double conversationLatitude) { this.conversationLatitude = conversationLatitude; }

    public Double getConversationLongitude() { return conversationLongitude; }

    public void setConversationLongitude(Double conversationLongitude) { this.conversationLongitude = conversationLongitude; }

    public boolean isConversationReadStatus() { return conversationReadStatus; }

    public void setConversationReadStatus(boolean conversationReadStatus) { this.conversationReadStatus = conversationReadStatus; }
}
