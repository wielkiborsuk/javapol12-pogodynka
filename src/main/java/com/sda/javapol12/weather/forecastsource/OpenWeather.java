package com.sda.javapol12.weather.forecastsource;

import org.apache.http.client.fluent.Request;

import java.io.IOException;
import java.time.LocalDate;

public class OpenWeather {
    private final static String URI_PATTERN = "https://api.openweathermap.org/data/2.5/onecall?lat=%f&lon=%f&exclude=minutely,hourly&units=metric&appid=%s";

    private final String key;

    public OpenWeather(String key) {
        this.key = key;
    }

    public String getForecast(double lat, double lon) {
        try {
            String uri = String.format(URI_PATTERN, lat, lon, key);

            return Request.Get(uri)
                    .execute().returnContent().asString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
