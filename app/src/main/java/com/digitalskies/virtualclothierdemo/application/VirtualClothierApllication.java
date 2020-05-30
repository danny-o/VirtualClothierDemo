package com.digitalskies.virtualclothierdemo.application;

import android.app.Application;
import android.content.Context;
import androidx.appcompat.app.AppCompatDelegate;

public class VirtualClothierApllication extends Application {
    private static VirtualClothierApllication instance;
    private static Context appContext;

    public static VirtualClothierApllication getInstance() {
        return instance;
    }

    public static Context getAppContext() {
        return appContext;
    }

    public void setAppContext(Context mAppContext) {
        this.appContext = mAppContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        this.setAppContext(getApplicationContext());
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }
}