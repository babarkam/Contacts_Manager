package com.example.contacts_manager.Utils;


import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    Context context;
    SharedPreferences sp;
    SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        this.context = context;
        sp = context.getSharedPreferences("Login Session", Context.MODE_PRIVATE);
        editor = sp.edit();
    }

    public void setData(String userId, String name, String email, String password, String DOB, String city, String country, String FCMtoken) {
        editor.putString("userId", userId);
        editor.putString("name", name);
        editor.putString("email", email);
        editor.putString("password", password);
        editor.putString("dateOfBirth", DOB);
        editor.putString("city", city);
        editor.putString("country", country);
        editor.putString("FCMtoken", FCMtoken);
        editor.putBoolean("login", true);

        editor.commit();
        editor.apply();
    }

    public String getuserId() { return sp.getString("userId", ""); }

    public String getName() { return sp.getString("name", ""); }

    public String getEmail() { return sp.getString("email", ""); }

    public String getPassword() { return sp.getString("password", ""); }

    public String getDateOfBirth() { return sp.getString("dateOfBirth", ""); }

    public String getCity() { return sp.getString("city", ""); }

    public String getCountry() { return sp.getString("country", ""); }

    public String getFCMtoken() { return sp.getString("FCMtoken", ""); }

    public void setFCMtoken(String token) {
        editor.putString("FCMtoken", token);
        editor.commit();
        editor.apply();
    }

    public Boolean getLoginStatus() { return sp.getBoolean("login", false); }


    public void logOut() {
        editor.clear();
        editor.commit();
        editor.apply();
    }

}
