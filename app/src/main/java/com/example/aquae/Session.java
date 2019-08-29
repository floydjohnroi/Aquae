package com.example.aquae;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Session {

    private SharedPreferences sharedPreferences;

    public Session(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

    }

    public void setId(String id) {
        sharedPreferences.edit().putString("id", id).apply();
    }

    public String getId() {
        return sharedPreferences.getString("id", null);
    }

    public void setFirstname(String firstname) {
        sharedPreferences.edit().putString("firstname", firstname).apply();
    }

    public String getFirstname() {
        return sharedPreferences.getString("firstname", null);
    }

    public void setLastname(String lastname) {
        sharedPreferences.edit().putString("lastname", lastname).apply();
    }

    public String getLastname() {
        return sharedPreferences.getString("lastname", null);
    }

    public void setUsername(String username) {
        sharedPreferences.edit().putString("username", username).apply();
    }

    public String getUsername() {
        return sharedPreferences.getString("username", null);
    }

    public void setPassword(String password) {
        sharedPreferences.edit().putString("password", password).apply();
    }
    public String getPassword() {
        return sharedPreferences.getString("password", null);
    }

    public void clearSession() {
        sharedPreferences.edit().clear().apply();
    }

    public boolean checkSession() {

        if(sharedPreferences.contains("id")) {
            return true;
        }

        return false;
    }
}
