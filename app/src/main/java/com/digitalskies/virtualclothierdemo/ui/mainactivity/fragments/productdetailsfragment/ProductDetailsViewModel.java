package com.digitalskies.virtualclothierdemo.ui.mainactivity.fragments.productdetailsfragment;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.digitalskies.virtualclothierdemo.models.User;
import com.digitalskies.virtualclothierdemo.utils.Event;
import com.digitalskies.virtualclothierdemo.data.Repository;
import com.digitalskies.virtualclothierdemo.models.Product;
import com.digitalskies.virtualclothierdemo.models.Response;


public class ProductDetailsViewModel extends AndroidViewModel {

    private final Repository repository;
    private final CartReminderStatus cartReminderStatus;

    public ProductDetailsViewModel(@NonNull Application application) {
        super(application);
        repository = Repository.getRepository(application);
        cartReminderStatus = CartReminderStatus.getCartReminderStatus();
    }
    public void updateFavorites(Product productFavoriteChanged){
        repository.updateFavorites(productFavoriteChanged);

    }
    public void addProductToCart(Product product){

        repository.updateCart(product,true);
    }
    public LiveData<Event<Response>> getResponse(){
        return repository.getResponse();
    }
    public boolean getCartReminderStatus(){
        return cartReminderStatus.getCartReminderScheduled();
    }
    public void setCartReminderScheduled(boolean status){
        cartReminderStatus.setCartReminderScheduled(status);
    }
    public void getUser(){
        repository.getUser();
    }

    public LiveData<Event<User>> getUserLiveData(){
        return repository.getUserLiveData();
    }

}
