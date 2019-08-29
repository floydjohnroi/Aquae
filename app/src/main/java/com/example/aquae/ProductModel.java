package com.example.aquae;

public class ProductModel {

    private String productName, productImage, refillPrice, purchasePrice;

    public ProductModel() {
    }

    public ProductModel(String productName, String productImage, String refillPrice, String purchasePrice) {
        this.productName = productName;
        this.productImage = productImage;
        this.refillPrice = refillPrice;
        this.purchasePrice = purchasePrice;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
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
}
