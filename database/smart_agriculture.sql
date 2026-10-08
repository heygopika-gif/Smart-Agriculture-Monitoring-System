-- OPTIONAL. The backend creates the database and table automatically on first run.
-- Use this only if you want to create them manually in MySQL Workbench.
CREATE DATABASE IF NOT EXISTS smart_agriculture;
USE smart_agriculture;

CREATE TABLE IF NOT EXISTS monitoring_records (
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    location           VARCHAR(60) NOT NULL,
    latitude           DOUBLE,
    longitude          DOUBLE,
    temperature        DOUBLE,
    humidity           DOUBLE,
    rainfall           DOUBLE,
    wind_speed         DOUBLE,
    weather_condition  VARCHAR(80),
    soil_moisture      DOUBLE,
    irrigation_status  VARCHAR(20),
    crop_health        VARCHAR(20),
    recommendation     VARCHAR(300),
    recorded_at        DATETIME(6) NOT NULL
);

-- See saved history:
-- SELECT * FROM monitoring_records ORDER BY recorded_at DESC;
