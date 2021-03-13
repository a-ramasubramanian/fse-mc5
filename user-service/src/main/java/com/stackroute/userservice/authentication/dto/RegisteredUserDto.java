package com.stackroute.userservice.authentication.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

/**
 * Object for receiving Login credentials
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisteredUserDto {

    @NotEmpty
    private String email;
    @NotEmpty
    private String password;
}
