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
