import React from "react";
import {
  CartesianGrid, Legend, Line, LineChart, ResponsiveContainer, Tooltip, XAxis, YAxis,
} from "recharts";

export default function HistoryChart({ history }) {
  if (history.length < 2) {
    return <p className="muted">Chart appears after at least 2 readings. Click Refresh again.</p>;
  }
  // API returns newest first; chart needs oldest first.
  const data = [...history].reverse().map((r) => ({
    time: new Date(r.recordedAt).toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" }),
    soil: r.soilMoisture,
    temp: r.temperature,
    humidity: r.humidity,
  }));
  return (
    <div style={{ width: "100%", height: 280 }}>
      <ResponsiveContainer>
        <LineChart data={data} margin={{ top: 10, right: 16, bottom: 0, left: 0 }}>
          <CartesianGrid strokeDasharray="3 3" />
          <XAxis dataKey="time" tick={{ fontSize: 12 }} />
          <YAxis tick={{ fontSize: 12 }} />
          <Tooltip />
          <Legend />
          <Line type="monotone" dataKey="soil" name="Soil moisture % (simulated)" stroke="#2b7de9" strokeWidth={2} />
          <Line type="monotone" dataKey="temp" name="Temperature °C" stroke="#e5732b" strokeWidth={2} />
          <Line type="monotone" dataKey="humidity" name="Humidity %" stroke="#2f9e6b" strokeWidth={2} />
        </LineChart>
      </ResponsiveContainer>
    </div>
  );
}
