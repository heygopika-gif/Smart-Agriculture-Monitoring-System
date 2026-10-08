import React from "react";

export default function HistoryTable({ history }) {
  if (!history.length) return <p className="muted">No records yet.</p>;
  return (
    <div className="table-wrap">
      <table>
        <thead>
          <tr>
            <th>Time</th>
            <th>Temp (°C)</th>
            <th>Humidity (%)</th>
            <th>Rain (mm)</th>
            <th>Wind (km/h)</th>
            <th>Weather</th>
            <th>Soil (%)</th>
            <th>Irrigation</th>
            <th>Crop Health</th>
          </tr>
        </thead>
        <tbody>
          {history.map((r) => (
            <tr key={r.id}>
              <td>{new Date(r.recordedAt).toLocaleString()}</td>
              <td>{r.temperature}</td>
              <td>{r.humidity}</td>
              <td>{r.rainfall}</td>
              <td>{r.windSpeed}</td>
              <td>{r.weatherCondition}</td>
              <td>{r.soilMoisture}</td>
              <td><span className={"pill " + r.irrigationStatus}>{r.irrigationStatus}</span></td>
              <td><span className={"pill " + r.cropHealth}>{r.cropHealth}</span></td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
