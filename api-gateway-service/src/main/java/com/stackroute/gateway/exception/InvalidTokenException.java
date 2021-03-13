package com.stackroute.gateway.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.UNAUTHORIZED, reason = "Invalid token")
public class InvalidTokenException extends Exception {

    public InvalidTokenException() {
    }

    public InvalidTokenException(String message) {
        super(message);
    }
}
