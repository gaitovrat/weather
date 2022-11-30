package com.example.weatherapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class MainFragment extends Fragment {
    private static final String TAG = "all";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private Utility utility;
    private Context context;
    private TextView temperatureTextView;
    private TextView descriptionTextView;
    private ImageView iconImageView;
    private EditText cityEditText;
    private JSONObject currentWeather;
    private RequestQueue requestQueue;
    private String city;

    public MainFragment() {}

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        this.context = context;
        this.utility = new Utility(this.context);
        this.requestQueue = Volley.newRequestQueue(this.context);
    }

    @Override
    public void onStart() {
        super.onStart();

        if (isNetworkConnected()) {
            updateWeather();
        } else {
            this.utility.noInternet();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        temperatureTextView = view.findViewById(R.id.temperature_textView);
        descriptionTextView = view.findViewById(R.id.description_textView);
        iconImageView = view.findViewById(R.id.icon_imageView);
        cityEditText = view.findViewById(R.id.city_editText);
        cityEditText.setText(R.string.defaultCity);
        city = cityEditText.getText().toString();

        Button button = view.findViewById(R.id.ok_button);
        button.setOnClickListener(this::okClick);

        return view;
    }

    private boolean isNetworkConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
        Network activeNetwork = connectivityManager.getActiveNetwork();
        NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork);

        return networkCapabilities != null &&
                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (requestQueue != null) {
            requestQueue.cancelAll(TAG);
        }
    }

    private void updateWeather() {
        if (this.getCity().isEmpty()) {
            return;
        }

        String givenCity = this.getCity();
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
            int resourceId = getResources().getIdentifier(icon, "drawable", context.getPackageName());
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
        setCity(this.cityEditText.getText().toString());

        MainActivity activity = (MainActivity) context;
        activity.setCity(this.getCity());

        if (isNetworkConnected()) {
            updateWeather();
        } else {
            this.utility.noInternet();
        }
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;

        updateWeather();
    }
}