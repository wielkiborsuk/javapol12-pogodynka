package com.sda.javapol12.weather.forecastcache;

import com.sda.javapol12.weather.DatabaseConfig;
import com.sda.javapol12.weather.model.CachedForecast;
import com.sda.javapol12.weather.model.WeatherForecast;
import com.sda.javapol12.weather.model.WeatherSource;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class CachedForecastDao {
    public void saveForecast(CachedForecast forecast) {
        try (Session session = DatabaseConfig.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            session.save(forecast);

            transaction.commit();
        }
    }

    @SuppressWarnings("unchecked")
    public List<CachedForecast> listForecast() {
        try (Session session = DatabaseConfig.getSessionFactory().openSession()) {
            List<CachedForecast> forecasts = session
                    .createQuery("select f from CachedForecast f").list();

            return forecasts;
        }
    }

    public CachedForecast findWeatherForecastForLocalization(WeatherSource source, String localization, LocalDate date) {
        LocalDateTime todayMidnight = LocalDate.now().atStartOfDay();
        try (Session session = DatabaseConfig.getSessionFactory().openSession()) {
            List<CachedForecast> forecasts = session
                    .createQuery("select f from CachedForecast f where " +
                            "f.source = :source and f.localization = :localization and f.date = :date " +
                            "and f.created > :todayMidnight")
                    .setParameter("source", source)
                    .setParameter("localization", localization)
                    .setParameter("date", date)
                    .setParameter("todayMidnight", todayMidnight)
                    .list();

            return forecasts.stream().findFirst().orElse(null);
        }
    }
}
