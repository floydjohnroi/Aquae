package com.example.aquae;

public class ProductModel {

    private String productName, productImage, minOrder, maxOrder;

    public ProductModel(String productName, String productImage, String minOrder, String maxOrder) {
        this.productName = productName;
        this.productImage = productImage;
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
