package com.digitalskies.virtualclothierdemo.ui.mainactivity.fragments.deliverydetailsfragment;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.digitalskies.virtualclothierdemo.data.Repository;
import com.digitalskies.virtualclothierdemo.models.User;
import com.digitalskies.virtualclothierdemo.utils.Event;

public class DeliveryDetailsViewModel extends AndroidViewModel {

    private Repository repository;
    public DeliveryDetailsViewModel(@NonNull Application application) {
        super(application);
        repository = Repository.getRepository(application);
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
