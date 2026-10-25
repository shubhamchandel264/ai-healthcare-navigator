package com.example.healthnavigator;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private final SharedPreferences sharedPreferences;

    public SessionManager(Context context) {
        sharedPreferences = context.getSharedPreferences(
                "health_app_session",
                Context.MODE_PRIVATE
        );
    }

    public void saveToken(String token) {
        sharedPreferences.edit()
                .putString("token", token)
                .apply();
    }

    public String getToken() {
        return sharedPreferences.getString("token", null);
    }

    public String getAuthHeader() {
        String token = getToken();

        if (token == null || token.isEmpty()) {
            return null;
        }

        return "Bearer " + token;
    }

    public boolean isLoggedIn() {
        String token = getToken();
        return token != null && !token.isEmpty();
    }

    public void clearSession() {
        sharedPreferences.edit()
                .clear()
                .apply();
    }
}