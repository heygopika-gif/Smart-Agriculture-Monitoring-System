package com.agri.controller;

import com.agri.entity.MonitoringRecord;
import com.agri.service.MonitoringService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"})
public class MonitoringController {

    private final MonitoringService service;

    public MonitoringController(MonitoringService service) {
        this.service = service;
    }

    /** GET /api/locations  -> ["Chennai","Coimbatore",...] */
    @GetMapping("/locations")
    public List<String> locations() {
        return new ArrayList<>(MonitoringService.LOCATIONS.keySet());
    }

    /** POST /api/monitor/check?location=Chennai -> new monitoring record (saved in MySQL) */
    @PostMapping("/monitor/check")
    public MonitoringRecord check(@RequestParam(defaultValue = "Chennai") String location) {
        return service.check(location);
    }

    /** GET /api/monitor/history?location=Chennai&limit=20 */
    @GetMapping("/monitor/history")
    public List<MonitoringRecord> history(@RequestParam(required = false) String location,
                                          @RequestParam(defaultValue = "20") int limit) {
        return service.history(location, limit);
    }

    /** DELETE /api/monitor/history -> clears all records (handy before a demo) */
    @DeleteMapping("/monitor/history")
    public ResponseEntity<Void> clear() {
        service.clearHistory();
        return ResponseEntity.noContent().build();
    }
}
