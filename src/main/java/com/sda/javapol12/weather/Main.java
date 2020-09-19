package com.sda.javapol12.weather;

import com.sda.javapol12.weather.forecastsource.OpenWeather;

import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        OpenWeather forecastSource = new OpenWeather("put your key in");


        System.out.println(forecastSource.getForecast(50.041187, 21.999121));

    }
}
