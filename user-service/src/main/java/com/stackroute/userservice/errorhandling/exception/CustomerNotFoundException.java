package com.stackroute.userservice.errorhandling.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Customer with given Id not found")
public class CustomerNotFoundException extends Exception {
    public CustomerNotFoundException() {
        super();
    }

    public CustomerNotFoundException(String message) {
        super(message);
    }
}
