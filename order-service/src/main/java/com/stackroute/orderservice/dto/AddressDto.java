package com.stackroute.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressDto {

    private String buildingNo;
    private String street;
    private String area;
    private String city;
    private String state;
    private int pincode;
    private String name;
    private long contactNumber;


}
