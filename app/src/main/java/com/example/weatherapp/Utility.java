package com.example.weatherapp;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.VolleyError;

public class Utility {
    private final Context context;

    public Utility(Context context) {
        this.context = context;
    }

    public void processError(VolleyError error) {
        String message;

        if (error != null) {
            message = String.valueOf(error.networkResponse.statusCode);
        } else {
            message = "404";
        }

        showNetworkMessage(message);
    }

    public void noInternet() {
        showNetworkMessage("No internet connection");
    }

    private void showNetworkMessage(String message) {
        showMessage("network", message);
    }

    private void showMessage(String tag, String message) {
        Log.e(tag, message);
        Toast.makeText(this.context, message, Toast.LENGTH_SHORT).show();
    }
}
