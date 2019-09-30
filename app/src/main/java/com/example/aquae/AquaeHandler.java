package com.example.aquae;

import android.app.Application;
import com.google.firebase.database.FirebaseDatabase;

public class AquaeHandler extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
