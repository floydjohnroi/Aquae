package com.example.aquae;

public class TrackOrderModel {

    String orderId, clientId, orderTime, status, deliveryAddress;

    public TrackOrderModel(String orderId, String clientId, String orderTime, String status, String deliveryAddress) {
        this.orderId = orderId;
        this.clientId = clientId;
        this.orderTime = orderTime;
        this.status = status;
        this.deliveryAddress = deliveryAddress;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }
}
