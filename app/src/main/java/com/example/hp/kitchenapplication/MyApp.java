package com.example.hp.kitchenapplication;

import android.app.Application;

import com.androidnetworking.AndroidNetworking;

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AndroidNetworking.initialize(getApplicationContext());
        AndroidNetworking.enableLogging();
    }
}
