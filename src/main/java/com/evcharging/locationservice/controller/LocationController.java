package com.evcharging.locationservice.controller;

import com.evcharging.locationservice.dto.LocationDTO;
import com.evcharging.locationservice.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/locations")
@CrossOrigin(origins = "*", maxAge = 3600)
public class LocationController {

    @Autowired
    private LocationService locationService;

    @PostMapping
    public ResponseEntity<?> saveLocation(@Valid @RequestBody LocationDTO locationDTO) {
        try {
            LocationDTO saved = locationService.saveLocation(locationDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "Location saved", "location", saved));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{entityId}/{entityType}")
    public ResponseEntity<?> getLocationByEntity(@PathVariable Long entityId, @PathVariable String entityType) {
        try {
            return ResponseEntity.ok(locationService.getLocationByEntity(entityId, entityType));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/type/{entityType}")
    public ResponseEntity<?> getLocationsByType(@PathVariable String entityType) {
        return ResponseEntity.ok(locationService.getLocationsByType(entityType));
    }

    @GetMapping("/city/{city}")
    public ResponseEntity<?> getLocationsByCity(@PathVariable String city) {
        return ResponseEntity.ok(locationService.getLocationsByCity(city));
    }

    @GetMapping("/distance")
    public ResponseEntity<?> calculateDistance(@RequestParam Double lat1, @RequestParam Double lon1,
                                               @RequestParam Double lat2, @RequestParam Double lon2) {
        try {
            Double distance = locationService.calculateDistance(lat1, lon1, lat2, lon2);
            return ResponseEntity.ok(Map.of(
                    "distance_meters", distance,
                    "distance_km", distance / 1000,
                    "distance_miles", distance / 1609.34
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{locationId}")
    public ResponseEntity<?> deleteLocation(@PathVariable Long locationId) {
        try {
            locationService.deleteLocation(locationId);
            return ResponseEntity.ok(Map.of("message", "Location deleted"));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
