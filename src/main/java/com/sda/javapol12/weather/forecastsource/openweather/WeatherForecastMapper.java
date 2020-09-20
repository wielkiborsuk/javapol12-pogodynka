package com.sda.javapol12.weather.forecastsource.openweather;

import com.sda.javapol12.weather.forecastsource.openweather.model.OpenWeatherDailyForecast;
import com.sda.javapol12.weather.model.WeatherForecast;

public class WeatherForecastMapper {
    public static WeatherForecast fromOpenWeatherForecast(OpenWeatherDailyForecast forecast) {
        WeatherForecast result = new WeatherForecast();

        result.setHumidity(forecast.getHumidity());
        result.setPressure(forecast.getPressure());
        result.setTemperature(forecast.getTemp());
        result.setWindDegree(forecast.getWindDegree());
        result.setWindSpeed(forecast.getWindSpeed());

        return result;
    }
}
