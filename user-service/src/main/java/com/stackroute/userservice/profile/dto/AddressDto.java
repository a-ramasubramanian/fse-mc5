package com.stackroute.userservice.profile.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressDto implements Serializable {

    private String buildingNo;
    private String street;
    private String area;
    private String city;
    private String state;
    private int pincode;
    private String name;
    private long contactNumber;


}
