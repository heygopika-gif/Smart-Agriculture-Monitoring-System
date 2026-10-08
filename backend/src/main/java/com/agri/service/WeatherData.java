package com.agri.service;

/** Real values returned by the WeatherAPI.com "current" block. */
public record WeatherData(double temperature, double humidity, double rainfall,
                          double windSpeed, String condition) {
}
