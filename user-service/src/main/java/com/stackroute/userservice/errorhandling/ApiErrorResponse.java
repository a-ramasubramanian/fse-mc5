package com.stackroute.userservice.errorhandling;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * Represent the standard API Error response
 */
@Data
@Builder
public class ApiErrorResponse {
    private int statusCode;
    private HttpStatus status;
    private String message;
    @Builder.Default
    private List<String> errors = new ArrayList<>();
}
