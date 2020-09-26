package com.sda.javapol12.weather.forecastsource.openweather;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sda.javapol12.weather.forecastcache.CachedForecastDao;
import com.sda.javapol12.weather.forecastsource.openweather.model.Coords;
import com.sda.javapol12.weather.forecastsource.openweather.model.OpenWeatherDailyForecast;
import com.sda.javapol12.weather.forecastsource.openweather.model.OpenWeatherResponse;
import com.sda.javapol12.weather.forecastsource.openweather.model.OpenWeatherWeatherResponse;
import com.sda.javapol12.weather.model.CachedForecast;
import com.sda.javapol12.weather.model.WeatherForecast;
import com.sda.javapol12.weather.model.WeatherSource;
import org.apache.http.client.fluent.Request;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

public class OpenWeather {
    private final static String URI_PATTERN = "https://api.openweathermap.org/data/2.5/onecall?lat=%f&lon=%f&exclude=minutely,hourly&units=metric&appid=%s";
    private final static String CITY_PATTERN = "https://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s";
    private final static ObjectMapper MAPPER = new ObjectMapper();

    private final String key;
    private final CachedForecastDao cache;

    public OpenWeather(String key) {
        this.key = key;
        this.cache = new CachedForecastDao();
    }

    public WeatherForecast getForecast(String city) {
        return getForecast(city, getTomorrow());
    }

    public WeatherForecast getForecast(String city, LocalDate date) {
        CachedForecast previousForecast = findCachedForecast(city, date);
        if (previousForecast != null) {
            System.out.println("Returning cached result!");
            return previousForecast.getForecast();
        }

        System.out.println("Using open weather to get forecast");
        WeatherForecast weatherForecast = findOpenWeatherForecast(city, date);

        saveCachedForecast(weatherForecast, city, date);
        return weatherForecast;
    }

    public WeatherForecast getForecast(double lat, double lon) {
        return getForecast(lat, lon, getTomorrow());
    }

    public WeatherForecast getForecast(double lat, double lon, LocalDate date) {
        CachedForecast previousForecast = findCachedForecast(lat, lon, date);
        if (previousForecast != null) {
            System.out.println("Returning cached result!");
            return previousForecast.getForecast();
        }

        System.out.println("Using open weather to get forecast");
        WeatherForecast weatherForecast = findOpenWeatherForecast(lat, lon, date);

        saveCachedForecast(weatherForecast, lat, lon, date);
        return weatherForecast;
    }

    private CachedForecast findCachedForecast(double lat, double lon, LocalDate date) {
        return findCachedForecast(getLocalization(lat, lon), date);
    }

    private CachedForecast findCachedForecast(String localization, LocalDate date) {
        return cache.findWeatherForecastForLocalization(WeatherSource.OPEN_WEATHER, localization, date);
    }

    private void saveCachedForecast(WeatherForecast weatherForecast, double lat, double lon, LocalDate date) {
        saveCachedForecast(weatherForecast, getLocalization(lat, lon), date);
    }

    private void saveCachedForecast(WeatherForecast weatherForecast, String localization, LocalDate date) {
        CachedForecast cachedForecast = new CachedForecast(weatherForecast, WeatherSource.OPEN_WEATHER, localization, date);
        cache.saveForecast(cachedForecast);
    }

    private String getLocalization(double lat, double lon) {
        return String.format("%f;%f", lat, lon);
    }

    private WeatherForecast findOpenWeatherForecast(double lat, double lon, LocalDate date) {
        try {
            String uri = String.format(URI_PATTERN, lat, lon, key);

            String response = Request.Get(uri)
                    .execute().returnContent().asString();
            OpenWeatherResponse openWeatherResponse = MAPPER.readValue(response, OpenWeatherResponse.class);
            OpenWeatherDailyForecast forecastForDate = findForecastForDate(openWeatherResponse.getDaily(), date);
            return WeatherForecastMapper.fromOpenWeatherForecast(forecastForDate);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private WeatherForecast findOpenWeatherForecast(String city, LocalDate date) {
        try {
            String uri = String.format(CITY_PATTERN, city, key);
            String response = Request.Get(uri)
                    .execute().returnContent().asString();
            Coords coords = MAPPER.readValue(response, OpenWeatherWeatherResponse.class).getCoord();
            return getForecast(coords.getLat(), coords.getLon(), date);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private OpenWeatherDailyForecast findForecastForDate(List<OpenWeatherDailyForecast> dailyForecasts, LocalDate date) {
        return dailyForecasts.stream()
                .filter(forecast -> date.equals(Instant.ofEpochSecond(forecast.getDateTime())
                        .atZone(ZoneId.systemDefault()).toLocalDate()))
                .findAny()
                .orElse(null);
    }

    private LocalDate getTomorrow() {
        return LocalDate.now().plusDays(1);
    }
}
