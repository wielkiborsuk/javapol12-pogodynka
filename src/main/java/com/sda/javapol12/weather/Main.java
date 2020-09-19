package com.sda.javapol12.weather;

import com.sda.javapol12.weather.forecastsource.openweather.OpenWeather;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) throws IOException {
        String key = Files.readAllLines(Paths.get("key.txt")).get(0).trim();
        OpenWeather forecastSource = new OpenWeather(key);

        System.out.println(forecastSource.getForecast(50.041187, 21.999121));
        System.out.println(forecastSource.getForecast("Rzeszow"));
    }
}
