package com.digitalskies.virtualclothierdemo.ui.productdetailsactivity;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.digitalskies.virtualclothierdemo.Event;
import com.digitalskies.virtualclothierdemo.Repository;


public class ProductDetailsViewModel extends AndroidViewModel {

    private final Repository repository;
    private final CartReminderStatus cartReminderStatus;

    public ProductDetailsViewModel(@NonNull Application application) {
        super(application);
        repository = Repository.getRepository(application);
        cartReminderStatus = CartReminderStatus.getCartReminderStatus();
    }
    public void updateFavorites(String name,Boolean isFavorite){
        repository.updateFavorites(name,isFavorite);

    }
    public void addProductToCart(String productName){
        repository.getCartProductsNames().add(productName);
        repository.updateCart();
    }
    public LiveData<Event<String>> favoritesUpdated(){
        return repository.getIfFavsUpdated();
    }
    public boolean getCartReminderStatus(){
        return cartReminderStatus.getCartReminderScheduled();
    }
    public void setCartReminderScheduled(boolean status){
        cartReminderStatus.setCartReminderScheduled(status);
    }

}
