package com.evcharging.locationservice.service;

import com.evcharging.locationservice.dto.LocationDTO;
import com.evcharging.locationservice.entity.Location;
import com.evcharging.locationservice.repository.LocationRepository;
import com.evcharging.locationservice.util.GoogleMapsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

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
    private RestTemplate restTemplate;

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
