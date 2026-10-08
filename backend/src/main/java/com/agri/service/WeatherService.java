package com.agri.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

/** Calls the WeatherAPI.com REST API (current.json) and reads the JSON response. */
@Service
public class WeatherService {

    private final RestClient client;
    private final String apiKey;

    public WeatherService(@Value("${weather.api.base-url}") String baseUrl,
                          @Value("${weather.api.key}") String apiKey) {
        this.apiKey = apiKey;
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(8000);
        factory.setReadTimeout(10000);
        this.client = RestClient.builder().baseUrl(baseUrl).requestFactory(factory).build();
    }

    public WeatherData fetch(double lat, double lon) {
        if (apiKey == null || apiKey.isBlank() || apiKey.startsWith("PASTE_")) {
            throw new IllegalStateException(
                    "WeatherAPI.com key not set. Put your key in application.properties (weather.api.key)");
        }
        JsonNode root = client.get()
                .uri(u -> u.path("/v1/current.json")
                        .queryParam("key", apiKey)
                        .queryParam("q", lat + "," + lon)
                        .queryParam("aqi", "no")
                        .build())
                .retrieve()
                .body(JsonNode.class);

        JsonNode c = root == null ? null : root.path("current");
        if (c == null || c.isMissingNode() || c.isNull()) {
            throw new IllegalStateException("WeatherAPI.com returned no current weather data");
        }
        return new WeatherData(
                c.path("temp_c").asDouble(),
                c.path("humidity").asDouble(),
                c.path("precip_mm").asDouble(),
                c.path("wind_kph").asDouble(),
                c.path("condition").path("text").asText("Unknown"));
    }
}
