package com.digitalskies.virtualclothierdemo.ui.mainactivity.fragments.profilefragment;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.digitalskies.virtualclothierdemo.data.Repository;
import com.digitalskies.virtualclothierdemo.models.Response;
import com.digitalskies.virtualclothierdemo.models.User;
import com.digitalskies.virtualclothierdemo.utils.Event;

import java.util.HashMap;

public class ProfileFragmentViewModel extends AndroidViewModel {

    private Repository repository;
    public ProfileFragmentViewModel(@NonNull Application application) {
        super(application);
        repository = Repository.getRepository(application);
    }

    public void updateUserData(HashMap<String,Object> data){

        repository.updateUserData(data);
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
}
