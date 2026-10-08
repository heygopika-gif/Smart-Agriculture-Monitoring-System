import React, { useCallback, useEffect, useState } from "react";
import { checkNow, clearHistory, getHistory, getLocations } from "./api.js";
import StatCard from "./components/StatCard.jsx";
import HistoryTable from "./components/HistoryTable.jsx";
import HistoryChart from "./components/HistoryChart.jsx";

const AUTO_REFRESH_MS = 60000; // new reading every 60 seconds

export default function App() {
  const [locations, setLocations] = useState([]);
  const [location, setLocation] = useState("Chennai");
  const [current, setCurrent] = useState(null);
  const [history, setHistory] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  // Take a new reading (backend: real weather + simulated soil), then reload history.
  const refresh = useCallback(async () => {
    setLoading(true);
    try {
      const record = await checkNow(location);
      setCurrent(record);
      setHistory(await getHistory(location, 20));
      setError("");
    } catch (e) {
      setError(e.message);
    } finally {
      setLoading(false);
    }
  }, [location]);

  useEffect(() => {
    getLocations().then(setLocations).catch((e) => setError(e.message));
  }, []);

  useEffect(() => {
    refresh();
    const timer = setInterval(refresh, AUTO_REFRESH_MS);
    return () => clearInterval(timer);
  }, [refresh]);

  async function handleClear() {
    if (!window.confirm("Delete all monitoring history?")) return;
    await clearHistory();
    setHistory([]);
    refresh();
  }

  const c = current;
  const irrigationTone = c ? { ON: "bad", DELAYED: "warn", OFF: "good" }[c.irrigationStatus] : "";
  const healthTone = c ? { GOOD: "good", MODERATE: "warn", POOR: "bad" }[c.cropHealth] : "";

  return (
    <div className="app">
      <header className="topbar">
        <div>
          <h1>Smart Agriculture Monitoring System</h1>
          <p className="sub">Real weather from WeatherAPI.com. Soil moisture is simulated (no physical sensors).</p>
        </div>
        <div className="controls">
          <select value={location} onChange={(e) => setLocation(e.target.value)}>
            {(locations.length ? locations : [location]).map((l) => (
              <option key={l}>{l}</option>
            ))}
          </select>
          <button className="btn" onClick={refresh} disabled={loading}>
            {loading ? "Checking..." : "Refresh"}
          </button>
        </div>
      </header>

      {error && <div className="error">{error}</div>}

      {c && (
        <>
          <p className="muted">
            {c.location} &middot; last checked {new Date(c.recordedAt).toLocaleString()}
          </p>

          <div className="cards">
            <StatCard label="Temperature" value={c.temperature} unit="°C" source="live" />
            <StatCard label="Humidity" value={c.humidity} unit="%" source="live" />
            <StatCard label="Rainfall" value={c.rainfall} unit="mm" source="live" />
            <StatCard label="Wind Speed" value={c.windSpeed} unit="km/h" source="live" />
            <StatCard label="Weather" value={c.weatherCondition} source="live" />
            <StatCard label="Soil Moisture" value={c.soilMoisture} unit="%" source="simulated" />
            <StatCard label="Irrigation" value={c.irrigationStatus} tone={irrigationTone} source="computed" />
            <StatCard label="Crop Health" value={c.cropHealth} tone={healthTone} source="computed" />
          </div>

          <div className="advice">
            <strong>Recommendation:</strong> {c.recommendation}
          </div>
        </>
      )}

      <section className="panel">
        <h2>Soil Moisture and Temperature Trend</h2>
        <HistoryChart history={history} />
      </section>

      <section className="panel">
        <div className="panel-head">
          <h2>Monitoring History ({location})</h2>
          <button className="btn ghost" onClick={handleClear}>Clear all history</button>
        </div>
        <HistoryTable history={history} />
      </section>
    </div>
  );
}
