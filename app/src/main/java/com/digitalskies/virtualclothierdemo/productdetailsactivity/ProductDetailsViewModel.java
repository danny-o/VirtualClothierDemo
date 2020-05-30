package com.digitalskies.virtualclothierdemo.productdetailsactivity;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.digitalskies.virtualclothierdemo.Repository;

import java.util.ArrayList;

public class ProductDetailsViewModel extends AndroidViewModel {

    private final Repository repository;

    public ProductDetailsViewModel(@NonNull Application application) {
        super(application);
        repository = Repository.getRepository();
    }
    public void updateFavorites(String name,Boolean isFavorite){
        repository.updateFavorites(name,isFavorite);
    }
    public LiveData<String> favoritesUpdated(){
        return repository.getIfFavsUpdated();
    }
}
