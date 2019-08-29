package com.example.aquae;

public class CartModel {

    private String client_id, client, min_order, max_order, ship_fee;

    public CartModel() {
    }

    public CartModel(String client_id, String client, String min_order, String max_order, String ship_fee) {
        this.client_id = client_id;
        this.client = client;
        this.min_order = min_order;
        this.max_order = max_order;
        this.ship_fee = ship_fee;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getMin_order() {
        return min_order;
    }

    public void setMin_order(String min_order) {
        this.min_order = min_order;
    }

    public String getMax_order() {
        return max_order;
    }

    public void setMax_order(String max_order) {
        this.max_order = max_order;
    }

    public String getShip_fee() {
        return ship_fee;
    }

    public void setShip_fee(String ship_fee) {
        this.ship_fee = ship_fee;
    }
}