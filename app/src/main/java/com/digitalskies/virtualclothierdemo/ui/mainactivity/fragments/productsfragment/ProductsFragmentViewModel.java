package com.digitalskies.virtualclothierdemo.ui.mainactivity.fragments.productsfragment;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.digitalskies.virtualclothierdemo.models.User;
import com.digitalskies.virtualclothierdemo.utils.Event;
import com.digitalskies.virtualclothierdemo.data.Repository;
import com.digitalskies.virtualclothierdemo.models.Product;
import com.digitalskies.virtualclothierdemo.models.Response;

import java.util.ArrayList;
import java.util.List;

public class ProductsFragmentViewModel extends AndroidViewModel {

    private final Repository repository;

    public ProductsFragmentViewModel(@NonNull Application application) {
        super(application);
        repository = Repository.getRepository(application);
    }
    public void getProducts(){
        repository.getProducts();
    }
    public ArrayList<Product> getFavoriteProducts(){
        return repository.getFavoriteProducts();
    }
    public void getFavAndCartProducts() {
        repository.getFavAndCartProducts();
    }

    public LiveData<List<Product>> products(){
        return repository.productList();
    }

    public void updateFavorites(Product productFavoriteChanged){
        repository.updateFavorites(productFavoriteChanged);

    }
    public LiveData<Event<Response>> getResponse(){
        return repository.getResponse();
    }

    public void getUser(){
        repository.getUser();
    }

    public LiveData<String> getUserProfileImage(){
        return repository.userProfileImage();
    }

}
