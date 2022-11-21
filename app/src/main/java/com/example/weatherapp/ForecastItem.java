package com.example.weatherapp;

public class ForecastItem {
    private String temp;
    private String time;
    private int icon;

    public ForecastItem(String temp, String time, int icon) {
        this.temp = temp;
        this.time = time;
        this.icon = icon;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    @Override
    public String toString() {
        return "Forecast{" +
                "temp='" + temp + '\'' +
                ", time='" + time + '\'' +
                ", icon=" + icon +
                '}';
    }
}
