package com.digitalskies.virtualclothierdemo.ui.checkoutactivity;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.digitalskies.virtualclothierdemo.Repository;
import com.digitalskies.virtualclothierdemo.models.Product;

import java.util.ArrayList;
import java.util.List;

public class CheckOutActivityViewModel extends AndroidViewModel {

    private final Repository repository;

    public CheckOutActivityViewModel(@NonNull Application application) {
        super(application);
        repository = Repository.getRepository(application);
    }

    public ArrayList<Product> getProductsInCart(){
        return repository.getProductsInCart();
    }

    public void updateCartProductNames(ArrayList<Product> productsInCart) {

        repository.updateCartProductNames(productsInCart);
    }
}
