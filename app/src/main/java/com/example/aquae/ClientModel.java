package com.example.aquae;

public class ClientModel {

    private String client_id, company, email, password, address, contact, storeImage, water_type, no_of_filter, ship_fee, kmAway;

    public ClientModel(String client_id, String company, String email, String password, String address, String contact, String storeImage, String water_type, String no_of_filter, String ship_fee, String kmAway) {
        this.client_id = client_id;
        this.company = company;
        this.email = email;
        this.password = password;
        this.address = address;
        this.contact = contact;
        this.storeImage = storeImage;
        this.water_type = water_type;
        this.no_of_filter = no_of_filter;
        this.ship_fee = ship_fee;
        this.kmAway = kmAway;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getStoreImage() {
        return storeImage;
    }

    public void setStoreImage(String storeImage) {
        this.storeImage = storeImage;
    }

    public String getWater_type() {
        return water_type;
    }

    public void setWater_type(String water_type) {
        this.water_type = water_type;
    }

    public String getNo_of_filter() {
        return no_of_filter;
    }

    public void setNo_of_filter(String no_of_filter) {
        this.no_of_filter = no_of_filter;
    }

    public String getShip_fee() {
        return ship_fee;
    }

    public void setShip_fee(String ship_fee) {
        this.ship_fee = ship_fee;
    }

    public String getKmAway() {
        return kmAway;
    }

    public void setKmAway(String kmAway) {
        this.kmAway = kmAway;
    }
}
