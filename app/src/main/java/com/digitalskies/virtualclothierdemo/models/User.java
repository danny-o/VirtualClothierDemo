package com.digitalskies.virtualclothierdemo.models;

import java.util.List;
import java.util.Map;

public class User {
    private String name;

    private Boolean isAdmin;

    private String deliveryAddress;



    private String image;

    private String email;

    private List<String> favoriteProducts;

    private List<String> productsInCart;

    public void setName(String name) {
        this.name = name;
    }

    public void setAdmin(Boolean admin) {
        isAdmin = admin;
    }

    public String getName() {
        return name;
    }

    public Boolean getAdmin() {
        return isAdmin;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
    public List<String> getFavoriteProducts() {
        return favoriteProducts;
    }

    public void setFavoriteProducts(List<String>favoriteProducts) {
        this.favoriteProducts = favoriteProducts;
    }
    public List<String> getProductsInCart() {
        return productsInCart;
    }

    public void setProductsInCart(List<String> productsInCart) {
        this.productsInCart = productsInCart;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }
}
