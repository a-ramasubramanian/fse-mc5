package com.stackroute.userservice.errorhandling.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT, reason = "Customer with given email already exists")
public class CustomerExistsException extends Exception {

    public CustomerExistsException() {
        super();
    }

    public CustomerExistsException(String message) {
        super(message);
    }
}
