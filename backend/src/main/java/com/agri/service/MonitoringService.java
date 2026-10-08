package com.agri.service;

import com.agri.entity.MonitoringRecord;
import com.agri.repository.MonitoringRecordRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class MonitoringService {

    /** Farm locations: name -> {latitude, longitude}. */
    public static final Map<String, double[]> LOCATIONS = new LinkedHashMap<>();
    static {
        LOCATIONS.put("Chennai", new double[]{13.0827, 80.2707});
        LOCATIONS.put("Coimbatore", new double[]{11.0168, 76.9558});
        LOCATIONS.put("Madurai", new double[]{9.9252, 78.1198});
        LOCATIONS.put("Thanjavur", new double[]{10.7870, 79.1378});
        LOCATIONS.put("Tiruchirappalli", new double[]{10.7905, 78.7047});
        LOCATIONS.put("Salem", new double[]{11.6643, 78.1460});
    }

    private final MonitoringRecordRepository repo;
    private final WeatherService weatherService;
    private final Random random = new Random();

    public MonitoringService(MonitoringRecordRepository repo, WeatherService weatherService) {
        this.repo = repo;
        this.weatherService = weatherService;
    }

    /** Fetch real weather, simulate soil, apply rules, save to MySQL, return the record. */
    public MonitoringRecord check(String location) {
        double[] ll = LOCATIONS.get(location);
        if (ll == null) {
            throw new IllegalArgumentException("Unknown location: " + location);
        }

        WeatherData w = weatherService.fetch(ll[0], ll[1]);

        Double previous = repo.findTopByLocationOrderByRecordedAtDesc(location)
                .map(MonitoringRecord::getSoilMoisture).orElse(null);
        double moisture = AgriRules.nextSoilMoisture(previous, w.rainfall(), w.temperature(), random);

        String irrigation = AgriRules.irrigationStatus(moisture, w.rainfall());
        String health = AgriRules.cropHealth(w.temperature(), w.humidity(), moisture, w.windSpeed());
        String advice = AgriRules.recommendation(irrigation, health, w.temperature(), moisture,
                w.rainfall(), w.windSpeed());

        MonitoringRecord r = new MonitoringRecord();
        r.setLocation(location);
        r.setLatitude(ll[0]);
        r.setLongitude(ll[1]);
        r.setTemperature(w.temperature());
        r.setHumidity(w.humidity());
        r.setRainfall(w.rainfall());
        r.setWindSpeed(w.windSpeed());
        r.setWeatherCondition(w.condition());
        r.setSoilMoisture(moisture);
        r.setIrrigationStatus(irrigation);
        r.setCropHealth(health);
        r.setRecommendation(advice);
        r.setRecordedAt(LocalDateTime.now());
        return repo.save(r);
    }

    public List<MonitoringRecord> history(String location, int limit) {
        int size = Math.max(1, Math.min(limit, 200));
        PageRequest page = PageRequest.of(0, size);
        if (location == null || location.isBlank()) {
            return repo.findAllByOrderByRecordedAtDesc(page);
        }
        return repo.findByLocationOrderByRecordedAtDesc(location, page);
    }

    public void clearHistory() {
        repo.deleteAll();
    }
}
