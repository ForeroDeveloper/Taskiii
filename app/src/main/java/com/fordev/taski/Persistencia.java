package com.fordev.taski;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class Persistencia extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

    }

}
