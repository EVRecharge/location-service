package com.evcharging.locationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReverseGeocodeRequest {
    @NotNull
    private Double latitude;
    @NotNull
    private Double longitude;
}
