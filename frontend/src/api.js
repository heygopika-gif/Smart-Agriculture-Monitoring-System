// REST calls to the Spring Boot backend (JSON). "/api" is proxied by Vite to localhost:8080.
async function request(path, options) {
  const res = await fetch("/api" + path, options);
  if (!res.ok) {
    let message = "Request failed (" + res.status + ")";
    try {
      const body = await res.json();
      if (body.error) message = body.error;
    } catch (e) {
      // response was not JSON
    }
    throw new Error(message);
  }
  return res.status === 204 ? null : res.json();
}

export const getLocations = () => request("/locations");

export const checkNow = (location) =>
  request("/monitor/check?location=" + encodeURIComponent(location), { method: "POST" });

export const getHistory = (location, limit = 20) =>
  request("/monitor/history?location=" + encodeURIComponent(location) + "&limit=" + limit);

export const clearHistory = () => request("/monitor/history", { method: "DELETE" });
