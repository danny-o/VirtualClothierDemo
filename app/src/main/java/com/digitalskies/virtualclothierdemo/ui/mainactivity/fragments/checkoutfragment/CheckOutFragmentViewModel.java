package com.digitalskies.virtualclothierdemo.ui.mainactivity.fragments.checkoutfragment;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.digitalskies.virtualclothierdemo.models.Response;
import com.digitalskies.virtualclothierdemo.models.User;
import com.digitalskies.virtualclothierdemo.utils.Event;
import com.digitalskies.virtualclothierdemo.data.Repository;
import com.digitalskies.virtualclothierdemo.models.Product;

import java.util.List;

public class CheckOutFragmentViewModel extends AndroidViewModel {

    private final Repository repository;

    private MutableLiveData<Event<List<Product>>> products=new MutableLiveData<>();


    public CheckOutFragmentViewModel(@NonNull Application application) {
        super(application);
        repository = Repository.getRepository(application);
    }

    public List<Product> getProductsInCart(){
        return repository.getProductsInCart();
    }



    public void updateCart(Product product,boolean addingToCart) {

        repository.updateCart(product,addingToCart);
    }

    public LiveData<Event<Response>> getResponse(){
        return repository.getResponse();
    }


    public void getUser(){
        repository.getUser();
    }

    public LiveData<Event<User>> getUserLiveData(){
        return repository.getUserLiveData();
    }

    public LiveData<String> getUserProfileImage(){
        return repository.userProfileImage();
    }
}
