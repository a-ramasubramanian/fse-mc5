package com.stackroute.userservice.authentication.service;

import com.stackroute.userservice.authentication.model.RegisteredUser;
import com.stackroute.userservice.authentication.repository.AuthenticationRepository;
import com.stackroute.userservice.errorhandling.exception.InvalidCredentialsException;
import com.stackroute.userservice.errorhandling.exception.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceImplTests {

    public static final String PASSWORD = "test";
    public static final String EMAIL = "test@test.com";

    @Mock
    private AuthenticationRepository authenticationRepository;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;
    private RegisteredUser user;
    private RegisteredUser encryptedPasswordUser;

    @BeforeEach
    public void setUp() {
        user = new RegisteredUser(1, EMAIL, PASSWORD);
        encryptedPasswordUser = new RegisteredUser(1, EMAIL, BCrypt.hashpw(PASSWORD, BCrypt.gensalt()));
    }

    @Test
    public void givenUserCredentialsWhenSavedSuccessfullyThenReturnRegisteredUser() {
        when(authenticationRepository.save(user)).thenReturn(user);
        RegisteredUser registeredUser = authenticationService.saveUserCredentials(user);
        assertThat(registeredUser).isEqualTo(user);
    }

    @Test
    public void givenUserCredentialsWhenAuthenticatedThenReturnRegisteredUser() throws InvalidCredentialsException, UserNotFoundException {
        when(authenticationRepository.getUserByEmail(EMAIL)).thenReturn(Optional.of(encryptedPasswordUser));
        RegisteredUser registeredUser = authenticationService.authenticateUser(user);
        assertThat(registeredUser).isEqualTo(encryptedPasswordUser);
    }

    @Test
    public void givenUserCredentialsWhenEmailNotFoundThenThrowException() throws InvalidCredentialsException, UserNotFoundException {
        when(authenticationRepository.getUserByEmail(EMAIL)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> authenticationService.authenticateUser(user)).isInstanceOf(UserNotFoundException.class);
    }

    @Test
    public void givenUserCredentialsWhenInvalidPasswordThenThrowException() throws InvalidCredentialsException, UserNotFoundException {
        user.setPassword("invalid");
        when(authenticationRepository.getUserByEmail(EMAIL)).thenReturn(Optional.of(encryptedPasswordUser));
        assertThatThrownBy(() -> authenticationService.authenticateUser(user)).isInstanceOf(InvalidCredentialsException.class);
    }
}