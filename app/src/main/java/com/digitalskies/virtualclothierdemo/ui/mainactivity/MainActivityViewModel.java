package com.digitalskies.virtualclothierdemo.ui.mainactivity;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.digitalskies.virtualclothierdemo.data.Repository;

public class MainActivityViewModel extends AndroidViewModel {

    Repository repository;
    public MainActivityViewModel(@NonNull Application application) {
        super(application);
        repository = Repository.getRepository(application);
    }

    public LiveData<String> getUserProfileImage(){
        return repository.userProfileImage();
    }


}
