package com.digitalskies.virtualclothierdemo.productdetailsactivity;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.digitalskies.virtualclothierdemo.Event;
import com.digitalskies.virtualclothierdemo.Repository;



public class ProductDetailsViewModel extends AndroidViewModel {

    private final Repository repository;

    public ProductDetailsViewModel(@NonNull Application application) {
        super(application);
        repository = Repository.getRepository(application);
    }
    public void updateFavorites(String name,Boolean isFavorite){
        repository.updateFavorites(name,isFavorite);

    }
    public void addProductToCart(String productName){
        repository.addProductToCart(productName);
    }
    public LiveData<Event<String>> favoritesUpdated(){
        return repository.getIfFavsUpdated();
    }
}
