package com.example.aquae;

public class ReminderModel {

    private String schedId, clientId, clientName, remindTime, pickupTime, image;

    public ReminderModel(String schedId, String clientId, String clientName, String remindTime, String pickupTime, String image) {
        this.schedId = schedId;
        this.clientId = clientId;
        this.clientName = clientName;
        this.remindTime = remindTime;
        this.pickupTime = pickupTime;
        this.image = image;
    }

    public String getSchedId() {
        return schedId;
    }

    public void setSchedId(String schedId) {
        this.schedId = schedId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getRemindTime() {
        return remindTime;
    }

    public void setRemindTime(String remindTime) {
        this.remindTime = remindTime;
    }

    public String getPickupTime() {
        return pickupTime;
    }

    public void setPickupTime(String pickupTime) {
        this.pickupTime = pickupTime;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
