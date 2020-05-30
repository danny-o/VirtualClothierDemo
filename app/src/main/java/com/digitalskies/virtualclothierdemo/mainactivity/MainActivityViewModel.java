package com.digitalskies.virtualclothierdemo.mainactivity;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.digitalskies.virtualclothierdemo.Repository;
import com.digitalskies.virtualclothierdemo.models.Product;

import java.util.ArrayList;
import java.util.List;

public class MainActivityViewModel extends AndroidViewModel {

    private final Repository repository;

    public MainActivityViewModel(@NonNull Application application) {
        super(application);
        repository = Repository.getRepository();
    }
    public void getProducts(){
        repository.getProducts();
    }
    public ArrayList<Product> getFavoriteProducts(){
        return repository.getFavoriteProducts();
    }
    public void getFavoriteProductsNames() {
        repository.getFavoriteProductsNames();
    }

    public LiveData<List<Product>> products(){
        return repository.productList();
    }


}
