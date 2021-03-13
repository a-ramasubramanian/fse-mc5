package com.stackroute.userservice.authentication.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents the response sent after successful authentication
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponseDto {
    private String token;
    private String email;
}
