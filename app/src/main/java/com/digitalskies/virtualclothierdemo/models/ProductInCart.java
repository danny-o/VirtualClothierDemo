package com.digitalskies.virtualclothierdemo.models;

public class ProductInCart{

    private int quantity;
    private String productName;

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }


    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }
}
