package com.stackroute.userservice.profile.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerDto {
    private String customerId;
    private String firstName;
    private String lastName;
    private String email;
    private AddressDto address;
    private List<OrderDto> orders;
}
