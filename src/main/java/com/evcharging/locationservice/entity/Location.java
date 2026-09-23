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
