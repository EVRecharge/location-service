// ============= LOCATION SERVICE APPLICATION =============
package com.evcharging.locationservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableDiscoveryClient
public class LocationServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(LocationServiceApplication.class, args);
    }
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}

// ============= LOCATION ENTITY =============
package com.evcharging.locationservice.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "locations", indexes = {
    @Index(name = "idx_locations_entity", columnList = "entity_id, entity_type"),
    @Index(name = "idx_locations_type", columnList = "entity_type"),
    @Index(name = "idx_locations_city", columnList = "city")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long locationId;

    @Column(nullable = false)
    private Long entityId;

    @Column(nullable = false)
    private String entityType;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column
    private String address;

    @Column
    private String city;

    @Column
    private String state;

    @Column
    private String zipCode;

    @Column
    private String name;
}

// ============= LOCATION REPOSITORY =============
package com.evcharging.locationservice.repository;

import com.evcharging.locationservice.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
    Optional<Location> findByEntityIdAndEntityType(Long entityId, String entityType);
    List<Location> findByEntityType(String entityType);
    List<Location> findByCity(String city);
}

// ============= LOCATION DTO =============
package com.evcharging.locationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocationDTO {
    private Long locationId;
    @NotNull
    private Long entityId;
    @NotNull
    private String entityType;
    @NotNull
    private Double latitude;
    @NotNull
    private Double longitude;
    private String address;
    private String city;
    private String state;
    private String zipCode;
    private String name;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class ReverseGeocodeRequest {
    @NotNull
    private Double latitude;
    @NotNull
    private Double longitude;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class ReverseGeocodeResponse {
    private String address;
    private String city;
    private String state;
    private String zipCode;
}

// ============= GOOGLE MAPS UTILITY =============
package com.evcharging.locationservice.util;

import org.springframework.stereotype.Component;

@Component
public class GoogleMapsUtil {

    public Double calculateDistance(Double lat1, Double lon1, Double lat2, Double lon2) {
        final int R = 6371;
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c * 1000;
    }

    public boolean isValidCoordinates(Double latitude, Double longitude) {
        return latitude != null && longitude != null &&
                latitude >= -90 && latitude <= 90 &&
                longitude >= -180 && longitude <= 180;
    }
}

// ============= LOCATION SERVICE =============
package com.evcharging.locationservice.service;

import com.evcharging.locationservice.dto.LocationDTO;
import com.evcharging.locationservice.entity.Location;
import com.evcharging.locationservice.repository.LocationRepository;
import com.evcharging.locationservice.util.GoogleMapsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LocationService {

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private GoogleMapsUtil googleMapsUtil;

    @Autowired
    private org.springframework.web.client.RestTemplate restTemplate;

    public LocationDTO saveLocation(LocationDTO locationDTO) {
        if (!googleMapsUtil.isValidCoordinates(locationDTO.getLatitude(), locationDTO.getLongitude())) {
            throw new RuntimeException("Invalid coordinates");
        }

        Location location = locationRepository
                .findByEntityIdAndEntityType(locationDTO.getEntityId(), locationDTO.getEntityType())
                .orElse(new Location());

        location.setEntityId(locationDTO.getEntityId());
        location.setEntityType(locationDTO.getEntityType());
        location.setLatitude(locationDTO.getLatitude());
        location.setLongitude(locationDTO.getLongitude());
        location.setAddress(locationDTO.getAddress());
        location.setCity(locationDTO.getCity());
        location.setState(locationDTO.getState());
        location.setZipCode(locationDTO.getZipCode());
        location.setName(locationDTO.getName());

        Location savedLocation = locationRepository.save(location);
        return convertToDTO(savedLocation);
    }

    public LocationDTO getLocationByEntity(Long entityId, String entityType) {
        Location location = locationRepository.findByEntityIdAndEntityType(entityId, entityType)
                .orElseThrow(() -> new RuntimeException("Location not found"));
        return convertToDTO(location);
    }

    public List<LocationDTO> getLocationsByType(String entityType) {
        return locationRepository.findByEntityType(entityType)
                .stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public List<LocationDTO> getLocationsByCity(String city) {
        return locationRepository.findByCity(city)
                .stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public Double calculateDistance(Double lat1, Double lon1, Double lat2, Double lon2) {
        if (!googleMapsUtil.isValidCoordinates(lat1, lon1) ||
            !googleMapsUtil.isValidCoordinates(lat2, lon2)) {
            throw new RuntimeException("Invalid coordinates");
        }
        return googleMapsUtil.calculateDistance(lat1, lon1, lat2, lon2);
    }

    public void deleteLocation(Long locationId) {
        if (!locationRepository.existsById(locationId)) {
            throw new RuntimeException("Location not found");
        }
        locationRepository.deleteById(locationId);
    }

    private LocationDTO convertToDTO(Location location) {
        LocationDTO dto = new LocationDTO();
        dto.setLocationId(location.getLocationId());
        dto.setEntityId(location.getEntityId());
        dto.setEntityType(location.getEntityType());
        dto.setLatitude(location.getLatitude());
        dto.setLongitude(location.getLongitude());
        dto.setAddress(location.getAddress());
        dto.setCity(location.getCity());
        dto.setState(location.getState());
        dto.setZipCode(location.getZipCode());
        dto.setName(location.getName());
        return dto;
    }
}

// ============= LOCATION CONTROLLER =============
package com.evcharging.locationservice.controller;

import com.evcharging.locationservice.dto.*;
import com.evcharging.locationservice.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
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
