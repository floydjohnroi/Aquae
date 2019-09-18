package com.example.aquae;

public class ScheduleProductModel {

    private String product, refillPrice, purchasePrice, refillQty, purchaseQty, waterType, image;

    public ScheduleProductModel(String product, String refillPrice, String purchasePrice, String refillQty, String purchaseQty, String waterType, String image) {
        this.product = product;
        this.refillPrice = refillPrice;
        this.purchasePrice = purchasePrice;
        this.refillQty = refillQty;
        this.purchaseQty = purchaseQty;
        this.waterType = waterType;
        this.image = image;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getRefillPrice() {
        return refillPrice;
    }

    public void setRefillPrice(String refillPrice) {
        this.refillPrice = refillPrice;
    }

    public String getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(String purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public String getRefillQty() {
        return refillQty;
    }

    public void setRefillQty(String refillQty) {
        this.refillQty = refillQty;
    }

    public String getPurchaseQty() {
        return purchaseQty;
    }

    public void setPurchaseQty(String purchaseQty) {
        this.purchaseQty = purchaseQty;
    }

    public String getWaterType() {
        return waterType;
    }

    public void setWaterType(String waterType) {
        this.waterType = waterType;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
