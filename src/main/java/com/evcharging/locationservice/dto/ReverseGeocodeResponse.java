package com.evcharging.locationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReverseGeocodeResponse {
    private String address;
    private String city;
    private String state;
    private String zipCode;
}
