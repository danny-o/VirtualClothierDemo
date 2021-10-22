package com.digitalskies.virtualclothierdemo.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.Exclude;

import java.util.List;

public class Product implements Parcelable {

    private  String image;
    private String name;
    private List<String> tags;
    private List<String> images;
    private  String productCategory;
    private int price;
    private String productDescription;



    @Exclude
    private boolean isFavorite;
    @Exclude
    private boolean isInCart;

    public Product(){

    }



    public Product(String name, String productCategory, int price,String productDescription,List<String> images,List<String> tags){
        this.name=name;
        this.price=price;
        this.productCategory=productCategory;
        this.productDescription=productDescription;
        this.images = images;
        this.tags = tags;
    }


    protected Product(Parcel in) {
        image = in.readString();
        name = in.readString();
        tags = in.createStringArrayList();
        images = in.createStringArrayList();
        productCategory = in.readString();
        price = in.readInt();
        productDescription = in.readString();
        isFavorite = in.readByte() != 0;
        isInCart = in.readByte() != 0;
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getProductDescription() {
        return productDescription;
    }
    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }
    public boolean isInCart() {
        return isInCart;
    }

    public void setInCart(boolean inCart) {
        isInCart = inCart;
    }




    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(image);
        dest.writeString(name);
        dest.writeStringList(tags);
        dest.writeStringList(images);
        dest.writeString(productCategory);
        dest.writeInt(price);
        dest.writeString(productDescription);
        dest.writeByte((byte) (isFavorite ? 1 : 0));
        dest.writeByte((byte) (isInCart ? 1 : 0));
    }
}
