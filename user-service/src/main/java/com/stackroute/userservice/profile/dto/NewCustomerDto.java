package com.stackroute.userservice.profile.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewCustomerDto {
    private String customerId;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private AddressDto address;
}
