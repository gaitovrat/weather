package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ForecastActivity extends AppCompatActivity {
    private static final String TAG = "all";
    private final Utility utility = new Utility(this);
    private int count = 0;
    private String city;
    private JSONObject forecast;
    private RequestQueue requestQueue;
    private List<ForecastItem> forecastArrayList;
    private ForecastRecyclerViewAdapter forecastRecyclerViewAdapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);

        Intent intent = getIntent();
        city = intent.getStringExtra("City");

        recyclerView = findViewById(R.id.forecast_recycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        forecastArrayList = new ArrayList<>();
        forecastRecyclerViewAdapter = new ForecastRecyclerViewAdapter(this, forecastArrayList);
        recyclerView.setAdapter(forecastRecyclerViewAdapter);

        requestQueue = Volley.newRequestQueue(this);
        getWeather();
    }


    @Override
    protected void onStop() {
        super.onStop();

        if (requestQueue != null) {
            requestQueue.cancelAll(TAG);
        }
    }

    private void getWeather() {
        String givenCity = city;
        String url = String.format("https://api.openweathermap.org/data/2.5/forecast?q=%s&appid=670c3aa6c07aaff1a2ad122818c5d0dd&units=metric", givenCity);

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url, null,
            response -> {
                try {
                    forecast = response;
                    count = forecast.getInt("cnt");
                    setWeather();
                } catch (Exception e) {
                    Log.e("Error", "Weather request error");
                }
            }, this.utility::processError
        );

        req.setTag(TAG);
        requestQueue.add(req);
    }

    private void setWeather() {
        try {
            for (int i = 0; i < count; i++) {
                String icon = "icon_" + forecast.getJSONArray("list").getJSONObject(i).getJSONArray("weather").getJSONObject(0).getString("icon");
                String time = forecast.getJSONArray("list").getJSONObject(i).getString("dt_txt");
                String description = forecast.getJSONArray("list").getJSONObject(i).getJSONArray("weather").getJSONObject(0).getString("description");
                String temperature = forecast.getJSONArray("list").getJSONObject(i).getJSONObject("main").getString("temp");
                String displayTemperature = temperature + " °C; " + description;
                int resourceId = getResources().getIdentifier(icon, "drawable", getPackageName());
                ForecastItem tempForecast = new ForecastItem(displayTemperature, time, resourceId);
                forecastArrayList.add(tempForecast);
            }
        } catch (JSONException e) {
            Log.e("json", e.getMessage());
        }

        forecastRecyclerViewAdapter = new ForecastRecyclerViewAdapter(this, forecastArrayList);
        recyclerView.setAdapter(forecastRecyclerViewAdapter);
    }
}