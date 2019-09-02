package com.example.aquae;

public class ProductModel {

    private String productName, productImage, refillPrice, purchasePrice, minOrder, maxOrder;

    public ProductModel(String productName, String productImage, String refillPrice, String purchasePrice, String minOrder, String maxOrder) {
        this.productName = productName;
        this.productImage = productImage;
        this.refillPrice = refillPrice;
        this.purchasePrice = purchasePrice;
        this.minOrder = minOrder;
        this.maxOrder = maxOrder;
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

    public String getMinOrder() {
        return minOrder;
    }

    public void setMinOrder(String minOrder) {
        this.minOrder = minOrder;
    }

    public String getMaxOrder() {
        return maxOrder;
    }

    public void setMaxOrder(String maxOrder) {
        this.maxOrder = maxOrder;
    }
}
