package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "all";
    private final Utility utility = new Utility(this);
    private TextView temperatureTextView;
    private TextView descriptionTextView;
    private ImageView iconImageView;
    private EditText cityEditText;
    private JSONObject currentWeather;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        temperatureTextView = findViewById(R.id.temperature_textView);
        descriptionTextView = findViewById(R.id.description_textView);
        iconImageView = findViewById(R.id.icon_imageView);
        cityEditText = findViewById(R.id.city_editText);
        requestQueue = Volley.newRequestQueue(this);

        cityEditText.setText(R.string.defaultCity);

        if (isNetworkConnected()) {
            updateWeather();
        } else {
            this.utility.noInternet();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (requestQueue != null) {
            requestQueue.cancelAll(TAG);
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        Network activeNetwork = connectivityManager.getActiveNetwork();
        NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork);

        return networkCapabilities != null &&
                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
    }

    private void updateWeather() {
        if (cityEditText.getText().toString().isEmpty()) {
            return;
        }

        String givenCity = cityEditText.getText().toString();
        String url = String.format("https://api.openweathermap.org/data/2.5/weather?q=%s&appid=670c3aa6c07aaff1a2ad122818c5d0dd&units=metric", givenCity);

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url, null,
            response -> {
                try {
                    currentWeather = response;
                    setWeather();
                } catch (Exception e) {
                    Log.e("error", "Weather request error");
                }
            }, this.utility::processError
        );

        req.setTag(TAG);
        requestQueue.add(req);
    }

    private void setWeather() {
        try {
            String icon = "icon_" + currentWeather.getJSONArray("weather").getJSONObject(0).getString("icon");
            int resourceId = getResources().getIdentifier(icon, "drawable", getPackageName());
            iconImageView.setImageResource(resourceId);

            String main = currentWeather.getJSONArray("weather").getJSONObject(0).getString("main");
            String description = currentWeather.getJSONArray("weather").getJSONObject(0).getString("description");
            String whole_description = main + "\n" + description;
            descriptionTextView.setText(whole_description);

            String currentTemperature = String.format("%s °C", currentWeather.getJSONObject("main").getString("temp"));
            String tempMin = String.format("%s °C", currentWeather.getJSONObject("main").getString("temp_min"));
            String tempMax = String.format("%s °C", currentWeather.getJSONObject("main").getString("temp_max"));
            String temperatures = currentTemperature + "\nMin: " + tempMin + ", Max: " + tempMax;
            temperatureTextView.setText(temperatures);
        } catch (JSONException e) {
            Log.e("error json", e.getMessage());
        }
    }

    public void okClick(View view) {
        if (isNetworkConnected()) {
            updateWeather();
        } else {
            this.utility.noInternet();
        }
    }

    public void openForecast(View view) {
        String givenCity = cityEditText.getText().toString();
        Intent intent = new Intent(this, ForecastActivity.class);
        intent.putExtra("City", givenCity);

        startActivity(intent);
    }
}