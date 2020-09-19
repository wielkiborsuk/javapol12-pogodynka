package com.sda.javapol12.weather.forecastsource.openweather;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sda.javapol12.weather.forecastsource.openweather.model.Coords;
import com.sda.javapol12.weather.forecastsource.openweather.model.OpenWeatherCurrentForecast;
import com.sda.javapol12.weather.forecastsource.openweather.model.OpenWeatherResponse;
import com.sda.javapol12.weather.forecastsource.openweather.model.OpenWeatherWeatherResponse;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import org.apache.http.client.fluent.Request;

import java.io.IOException;

public class OpenWeather {
    private final static String URI_PATTERN = "https://api.openweathermap.org/data/2.5/onecall?lat=%f&lon=%f&exclude=minutely,hourly&units=metric&appid=%s";
    private final static String CITY_PATTERN = "https://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s";
    private final static ObjectMapper MAPPER = new ObjectMapper();

    private final String key;

    public OpenWeather(String key) {
        this.key = key;
    }

    public OpenWeatherCurrentForecast getForecast(String city) {
        try {
            String uri = String.format(CITY_PATTERN, city, key);
            String response = Request.Get(uri)
                    .execute().returnContent().asString();
            Coords coords = MAPPER.readValue(response, OpenWeatherWeatherResponse.class).getCoord();
            return getForecast(coords.getLat(), coords.getLon());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public OpenWeatherCurrentForecast getForecast(double lat, double lon) {
        try {
            String uri = String.format(URI_PATTERN, lat, lon, key);

            String response = Request.Get(uri)
                    .execute().returnContent().asString();
            OpenWeatherResponse openWeatherResponse = MAPPER.readValue(response, OpenWeatherResponse.class);
            return openWeatherResponse.getCurrent();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
