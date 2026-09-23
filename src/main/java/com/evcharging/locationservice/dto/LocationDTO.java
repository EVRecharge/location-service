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
