package com.stackroute.gateway.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Missing or invalid Authorization header")
public class InvalidHeaderException extends Exception {
}
