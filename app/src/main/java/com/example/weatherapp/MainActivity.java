package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private String city;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.city = getString(R.string.defaultCity);

        BottomNavigationView bottomNavigationView = this.findViewById(R.id.navigation);
        bottomNavigationView.setOnItemSelectedListener(this::selected);

        if (savedInstanceState == null) {
            this.startFragment(MainFragment.class);
        }
    }

    private boolean selected(MenuItem item) {
        switch(item.getItemId()) {
        case R.id.forecast_item:
            this.startFragment(ForecastFragment.class, city);
            break;
        case R.id.weather_item:
            this.startFragment(MainFragment.class);
            break;
        default:
            break;
        }
        return true;
    }

    public void startFragment(Class<? extends Fragment> fragmentClass) {
        Fragment fragment = FragmentFactory.newInstance(fragmentClass);
        this.getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.base_fragment, fragment)
                .commit();

    }

    public void startFragment(Class<? extends Fragment> fragmentClass, String... args) {
        Fragment fragment = FragmentFactory.newInstance(fragmentClass, args);
        this.getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.base_fragment, fragment)
                .commit();

    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}