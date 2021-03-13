package com.stackroute.userservice.authentication.service;

import com.stackroute.userservice.authentication.model.RegisteredUser;
import com.stackroute.userservice.authentication.repository.AuthenticationRepository;
import com.stackroute.userservice.errorhandling.exception.InvalidCredentialsException;
import com.stackroute.userservice.errorhandling.exception.UserNotFoundException;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service class for saving customer credentials and authenticating Customers
 */
@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());
    private final AuthenticationRepository authenticationRepository;

    @Autowired
    public AuthenticationServiceImpl(AuthenticationRepository authenticationRepository) {
        this.authenticationRepository = authenticationRepository;
    }

    /**
     * Service method for authenticating a registered user
     */
    @Override
    public RegisteredUser authenticateUser(RegisteredUser registeredUser) throws UserNotFoundException, InvalidCredentialsException {
        logger.error(registeredUser.getEmail());

        RegisteredUser existingUser = authenticationRepository.getUserByEmail(registeredUser.getEmail()).orElseThrow(UserNotFoundException::new);
        if (!BCrypt.checkpw(registeredUser.getPassword(), existingUser.getPassword())) {
            throw new InvalidCredentialsException("Invalid Credentials");
        }
        return existingUser;
    }

    /**
     * Service method for saving customer credentials
     */
    @Override
    public RegisteredUser saveUserCredentials(RegisteredUser registeredUser) {
        registeredUser.setPassword(BCrypt.hashpw(registeredUser.getPassword(), BCrypt.gensalt()));
        return authenticationRepository.save(registeredUser);
    }

    /**
     * Service method for updating password
     */
    @Override
    public RegisteredUser updateUserCredentials(RegisteredUser registeredUser) throws UserNotFoundException {
        RegisteredUser validUser = authenticationRepository.getUserByEmail(registeredUser.getEmail()).orElseThrow(UserNotFoundException::new);
        validUser.setPassword(BCrypt.hashpw(validUser.getPassword(), BCrypt.gensalt()));
        return authenticationRepository.save(validUser);
    }
}