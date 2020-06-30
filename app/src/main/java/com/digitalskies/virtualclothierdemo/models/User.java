package com.digitalskies.virtualclothierdemo.models;

import java.util.List;
import java.util.Map;

public class User {
    private String name;

    private Boolean isAdmin;

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


}
