package com.example.aquae;

public class CoordinatesModel {

    String name, waterType, address;

    public CoordinatesModel(String name, String waterType, String address) {
        this.name = name;
        this.waterType = waterType;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWaterType() {
        return waterType;
    }

    public void setWaterType(String waterType) {
        this.waterType = waterType;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
