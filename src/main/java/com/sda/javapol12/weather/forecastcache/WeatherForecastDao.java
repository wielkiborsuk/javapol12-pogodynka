package com.sda.javapol12.weather.forecastcache;

import com.sda.javapol12.weather.DatabaseConfig;
import com.sda.javapol12.weather.model.WeatherForecast;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class WeatherForecastDao {
    public void saveForecast(WeatherForecast forecast) {
        try (Session session = DatabaseConfig.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            session.save(forecast);

            transaction.commit();
        }
    }

    @SuppressWarnings("unchecked")
    public List<WeatherForecast> listForecast() {
        try (Session session = DatabaseConfig.getSessionFactory().openSession()) {
            List<WeatherForecast> forecasts = session
                    .createQuery("select w from WeatherForecast w").list();

            return forecasts;
        }
    }
}
