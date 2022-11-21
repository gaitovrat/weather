package com.example.weatherapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ForecastRecyclerViewAdapter extends RecyclerView.Adapter<ForecastViewHolder>{
    private final List<ForecastItem> data;
    private final LayoutInflater layoutInflater;

    public ForecastRecyclerViewAdapter(Context context, List<ForecastItem> data) {
        this.layoutInflater = LayoutInflater.from(context);
        this.data = data;
    }

    @NonNull
    @Override
    public ForecastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rowItem = layoutInflater.inflate(R.layout.forecast_row, parent, false);
        return new ForecastViewHolder(rowItem);
    }

    @Override
    public void onBindViewHolder(ForecastViewHolder holder, int position) {
        ForecastItem forecastItem = data.get(position);

        holder.getTemp().setText(forecastItem.getTemp());
        holder.getTime().setText(String.valueOf(forecastItem.getTime()));
        holder.getIcon().setImageResource(forecastItem.getIcon());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
