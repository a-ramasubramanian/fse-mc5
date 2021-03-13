package com.stackroute.userservice.errorhandling;

import com.stackroute.userservice.errorhandling.exception.CustomerExistsException;
import com.stackroute.userservice.errorhandling.exception.CustomerNotFoundException;
import com.stackroute.userservice.errorhandling.exception.InvalidCredentialsException;
import com.stackroute.userservice.errorhandling.exception.UserNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Exception handler for the REST API
 */
@ControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleProductNotFoundException(CustomerNotFoundException exception, WebRequest request) {
        ApiErrorResponse errorResponse = buildErrorResponse(exception.getLocalizedMessage(), HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(errorResponse, errorResponse.getStatus());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleUserNotFoundException(UserNotFoundException exception, WebRequest request) {
        ApiErrorResponse errorResponse = buildErrorResponse(exception.getLocalizedMessage(), HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(errorResponse, errorResponse.getStatus());
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidCredentialsException(InvalidCredentialsException exception, WebRequest request) {
        ApiErrorResponse errorResponse = buildErrorResponse(exception.getLocalizedMessage(), HttpStatus.UNAUTHORIZED);
        return new ResponseEntity<>(errorResponse, errorResponse.getStatus());
    }

    @ExceptionHandler(CustomerExistsException.class)
    public ResponseEntity<ApiErrorResponse> handleProductExistsException(CustomerExistsException exception, WebRequest request) {
        ApiErrorResponse errorResponse = buildErrorResponse(exception.getLocalizedMessage(), HttpStatus.CONFLICT);
        return new ResponseEntity<>(errorResponse, errorResponse.getStatus());
    }

    /**
     * Exception handler for Invalid data in Http Request
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception, HttpHeaders headers, HttpStatus status, WebRequest request) {
        List<String> validationMessages = new ArrayList<>();
        for (ObjectError error : exception.getBindingResult().getAllErrors()) {
            validationMessages.add(error.getDefaultMessage());
        }

        ApiErrorResponse errorResponse = buildErrorResponse(exception.getMessage(), HttpStatus.BAD_REQUEST);
        errorResponse.getErrors().addAll(validationMessages);

        return handleExceptionInternal(exception, errorResponse, headers, errorResponse.getStatus(), request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleAllException(Exception exception, WebRequest request) {
        ApiErrorResponse errorResponse = buildErrorResponse(exception.getLocalizedMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(errorResponse, errorResponse.getStatus());
    }

    /**
     * Building standard error response for the exceptions in the API
     */
    private ApiErrorResponse buildErrorResponse(String message, HttpStatus httpStatus) {
        return ApiErrorResponse.builder()
                .status(httpStatus)
                .statusCode(httpStatus.value())
                .message(message)
                .build();

    }
}