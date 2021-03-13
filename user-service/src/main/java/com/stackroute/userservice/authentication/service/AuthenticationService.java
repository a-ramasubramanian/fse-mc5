package com.stackroute.userservice.authentication.service;

import com.stackroute.userservice.authentication.model.RegisteredUser;
import com.stackroute.userservice.errorhandling.exception.InvalidCredentialsException;
import com.stackroute.userservice.errorhandling.exception.UserNotFoundException;

public interface AuthenticationService {

    RegisteredUser authenticateUser(RegisteredUser registeredUser) throws UserNotFoundException, InvalidCredentialsException;

    RegisteredUser saveUserCredentials(RegisteredUser registeredUser);

    RegisteredUser updateUserCredentials(RegisteredUser registeredUser) throws UserNotFoundException;

}
