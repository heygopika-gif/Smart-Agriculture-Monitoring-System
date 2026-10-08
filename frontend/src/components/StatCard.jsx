import React from "react";

const SOURCE_LABEL = {
  live: "Live: WeatherAPI.com",
  simulated: "Simulated",
  computed: "Computed by rules",
};

export default function StatCard({ label, value, unit, tone, source }) {
  return (
    <div className={"card " + (tone || "")}>
      <div className="card-label">{label}</div>
      <div className="card-value">
        {value ?? "-"}
        {unit && value != null ? <span className="unit"> {unit}</span> : null}
      </div>
      <div className={"tag " + source}>{SOURCE_LABEL[source]}</div>
    </div>
  );
}
