# Smart Agriculture Monitoring System

React (Vite) + Java Spring Boot + MySQL + WeatherAPI.com API. Runs locally, no Docker.

## Data sources (be clear in viva)
| Data | Source |
|---|---|
| Temperature, humidity, rainfall, wind speed, weather condition | REAL, WeatherAPI.com API |
| Soil moisture | SIMULATED by backend (no physical sensors) |
| Irrigation status, crop health, recommendation | COMPUTED by backend rules (`AgriRules.java`) |
| Monitoring history | Saved in MySQL table `monitoring_records` |

## Requirements
JDK 21, Maven, Node.js 18+, MySQL 8 (running on port 3306), internet and a free WeatherAPI.com key.

## Run
**0. WeatherAPI.com key**: sign up free at https://www.weatherapi.com/signup.aspx, copy the API key, and set
`weather.api.key` in `backend/src/main/resources/application.properties`.

**1. MySQL**: start the server. In the same file set `spring.datasource.password` to your MySQL root
password. Database `smart_agriculture` and the table are created automatically.

**2. Backend** (terminal 1)
```
cd backend
mvn spring-boot:run
```
Test: http://localhost:8080/api/locations

**3. Frontend** (terminal 2)
```
cd frontend
npm install
npm run dev
```
Open http://localhost:5173

## REST API (JSON)
| Method | URL | Purpose |
|---|---|---|
| GET | /api/locations | list of farm locations |
| POST | /api/monitor/check?location=Chennai | fetch weather, simulate soil, apply rules, save to MySQL, return record |
| GET | /api/monitor/history?location=Chennai&limit=20 | saved history (newest first) |
| DELETE | /api/monitor/history | clear history |

## Flow
React `Refresh` (also every 60 s) -> `POST /api/monitor/check` -> `WeatherService` calls WeatherAPI.com
-> `AgriRules` simulates soil moisture, decides irrigation + crop health + advice
-> saved by Spring Data JPA into MySQL -> JSON returned -> dashboard cards, chart, history table.

## Rules (AgriRules.java)
- Soil moisture: previous value - drying (more when hot) + rain x 3 + small random noise. Range 5 to 95.
- Irrigation: moisture < 30 and little rain = ON; moisture < 30 with rain >= 2 mm = DELAYED; else OFF.
- Crop health: score starts at 100; penalties for temperature outside 15-35, humidity outside 35-90,
  soil moisture < 30 or > 80, wind > 40 km/h. Score >= 75 GOOD, >= 50 MODERATE, else POOR.

## Troubleshooting
- `Access denied for user 'root'`: fix the password in `application.properties`.
- Dashboard shows "Weather service unavailable": no internet, wrong/missing WeatherAPI.com key, or API blocked; no fake weather is shown.
- Port 8080 busy: change `server.port` and the proxy target in `frontend/vite.config.js`.

## Folder structure
```
backend/   Spring Boot (entity, repository, service, controller, config)
frontend/  React + Vite (App, components, api.js, styles.css)
database/  optional manual SQL script
```
