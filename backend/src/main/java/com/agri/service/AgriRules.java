package com.agri.service;

import java.util.Random;

/**
 * Pure business rules (no Spring). Easy to explain in viva:
 *  1. soil moisture simulation
 *  2. irrigation decision
 *  3. crop health score
 *  4. recommendation text
 */
public final class AgriRules {

    private AgriRules() {}

    // ---------- 1. Simulated soil moisture ----------
    /**
     * Soil moisture is SIMULATED. It starts at a random value, then every check it
     * dries a little (more when hot) and rises when it rains, plus a small random noise.
     */
    public static double nextSoilMoisture(Double previous, double rainfallMm, double tempC, Random rnd) {
        double base = previous != null ? previous : 40 + rnd.nextDouble() * 30; // first value 40..70
        double drying = 0.8 + Math.max(0, tempC - 20) * 0.1;                    // hotter = dries faster
        double wetting = rainfallMm * 3.0;                                      // rain adds moisture
        double noise = (rnd.nextDouble() - 0.5) * 4;                            // -2..+2
        double value = base - drying + wetting + noise;
        return round1(Math.max(5, Math.min(95, value)));
    }

    // ---------- 2. Irrigation status ----------
    public static String irrigationStatus(double soilMoisture, double rainfallMm) {
        if (soilMoisture < 30) {
            return rainfallMm >= 2 ? "DELAYED" : "ON";
        }
        return "OFF";
    }

    // ---------- 3. Crop health ----------
    public static String cropHealth(double temp, double humidity, double soilMoisture, double wind) {
        int score = 100;
        if (temp < 15 || temp > 35) score -= 25;
        if (temp > 40 || temp < 8) score -= 15;
        if (humidity < 35 || humidity > 90) score -= 15;
        if (soilMoisture < 30) score -= 25;
        if (soilMoisture > 80) score -= 20;
        if (wind > 40) score -= 15;
        if (score >= 75) return "GOOD";
        if (score >= 50) return "MODERATE";
        return "POOR";
    }

    // ---------- 4. Recommendation ----------
    public static String recommendation(String irrigation, String health, double temp,
                                        double soilMoisture, double rainfallMm, double wind) {
        StringBuilder sb = new StringBuilder();
        switch (irrigation) {
            case "ON" -> sb.append("Soil is dry (").append(soilMoisture).append("%). Start irrigation now. ");
            case "DELAYED" -> sb.append("Soil is dry but rain is falling. Delay irrigation and re-check later. ");
            default -> {
                if (soilMoisture > 80) sb.append("Soil is waterlogged. Stop irrigation and check drainage. ");
                else sb.append("Soil moisture is adequate. No irrigation needed. ");
            }
        }
        if (temp > 35) sb.append("High heat: irrigate in early morning or evening. ");
        if (temp < 15) sb.append("Low temperature: protect sensitive crops. ");
        if (wind > 40) sb.append("Strong wind: avoid spraying pesticides. ");
        if (rainfallMm >= 10) sb.append("Heavy rain: avoid fertilizer application. ");
        sb.append("Crop health is ").append(health).append(".");
        return sb.toString();
    }

    // ---------- Weather code (WMO) to text ----------
    public static String describeWeather(int code) {
        return switch (code) {
            case 0 -> "Clear sky";
            case 1 -> "Mainly clear";
            case 2 -> "Partly cloudy";
            case 3 -> "Overcast";
            case 45, 48 -> "Fog";
            case 51, 53, 55 -> "Drizzle";
            case 56, 57 -> "Freezing drizzle";
            case 61 -> "Light rain";
            case 63 -> "Rain";
            case 65 -> "Heavy rain";
            case 66, 67 -> "Freezing rain";
            case 71, 73, 75, 77 -> "Snow";
            case 80, 81, 82 -> "Rain showers";
            case 85, 86 -> "Snow showers";
            case 95 -> "Thunderstorm";
            case 96, 99 -> "Thunderstorm with hail";
            default -> "Unknown";
        };
    }

    public static double round1(double v) {
        return Math.round(v * 10.0) / 10.0;
    }
}
