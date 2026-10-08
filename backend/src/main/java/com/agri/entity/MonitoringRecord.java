package com.agri.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/** One monitoring snapshot: real weather + simulated soil data + computed decisions. */
@Entity
@Table(name = "monitoring_records")
public class MonitoringRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 60)
    private String location;

    private Double latitude;
    private Double longitude;

    // ---- REAL data (WeatherAPI.com) ----
    private Double temperature;      // C
    private Double humidity;         // %
    private Double rainfall;         // mm
    private Double windSpeed;        // km/h

    @Column(length = 80)
    private String weatherCondition;

    // ---- SIMULATED data ----
    private Double soilMoisture;     // %

    // ---- Computed by backend rules ----
    @Column(length = 20)
    private String irrigationStatus; // ON / OFF / DELAYED

    @Column(length = 20)
    private String cropHealth;       // GOOD / MODERATE / POOR

    @Column(length = 300)
    private String recommendation;

    @Column(nullable = false)
    private LocalDateTime recordedAt = LocalDateTime.now();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
    public Double getTemperature() { return temperature; }
    public void setTemperature(Double temperature) { this.temperature = temperature; }
    public Double getHumidity() { return humidity; }
    public void setHumidity(Double humidity) { this.humidity = humidity; }
    public Double getRainfall() { return rainfall; }
    public void setRainfall(Double rainfall) { this.rainfall = rainfall; }
    public Double getWindSpeed() { return windSpeed; }
    public void setWindSpeed(Double windSpeed) { this.windSpeed = windSpeed; }
    public String getWeatherCondition() { return weatherCondition; }
    public void setWeatherCondition(String weatherCondition) { this.weatherCondition = weatherCondition; }
    public Double getSoilMoisture() { return soilMoisture; }
    public void setSoilMoisture(Double soilMoisture) { this.soilMoisture = soilMoisture; }
    public String getIrrigationStatus() { return irrigationStatus; }
    public void setIrrigationStatus(String irrigationStatus) { this.irrigationStatus = irrigationStatus; }
    public String getCropHealth() { return cropHealth; }
    public void setCropHealth(String cropHealth) { this.cropHealth = cropHealth; }
    public String getRecommendation() { return recommendation; }
    public void setRecommendation(String recommendation) { this.recommendation = recommendation; }
    public LocalDateTime getRecordedAt() { return recordedAt; }
    public void setRecordedAt(LocalDateTime recordedAt) { this.recordedAt = recordedAt; }
}
