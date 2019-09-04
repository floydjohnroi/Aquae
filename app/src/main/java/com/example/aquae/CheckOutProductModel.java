package com.example.aquae;

public class CheckOutProductModel {

    private String client_id, product_id, product, refillQuantity, purchaseQuantity, waterType, refillPrice, purchasePrice, image, subtotal;

    public CheckOutProductModel(String client_id, String product_id, String product, String refillQuantity, String purchaseQuantity, String waterType, String refillPrice, String purchasePrice, String image, String subtotal) {
        this.client_id = client_id;
        this.product_id = product_id;
        this.product = product;
        this.refillQuantity = refillQuantity;
        this.purchaseQuantity = purchaseQuantity;
        this.waterType = waterType;
        this.refillPrice = refillPrice;
        this.purchasePrice = purchasePrice;
        this.image = image;
        this.subtotal = subtotal;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getRefillQuantity() {
        return refillQuantity;
    }

    public void setRefillQuantity(String refillQuantity) {
        this.refillQuantity = refillQuantity;
    }

    public String getPurchaseQuantity() {
        return purchaseQuantity;
    }

    public void setPurchaseQuantity(String purchaseQuantity) {
        this.purchaseQuantity = purchaseQuantity;
    }

    public String getWaterType() {
        return waterType;
    }

    public void setWaterType(String waterType) {
        this.waterType = waterType;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(String subtotal) {
        this.subtotal = subtotal;
    }

}
