package com.stackroute.userservice.authentication.repository;

import com.stackroute.userservice.authentication.model.RegisteredUser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ExtendWith(SpringExtension.class)
public class AuthenticationRepositoryEmbeddedH2Tests {

    public static final String PASSWORD = "test";
    public static final String EMAIL = "test@test.com";

    @Autowired
    private AuthenticationRepository authenticationRepository;
    private RegisteredUser user;

    @BeforeEach
    public void dataSetUp() {
        user = new RegisteredUser(1, EMAIL, PASSWORD);
        authenticationRepository.save(user);
    }

    @AfterEach
    public void tearDown() {
        user = null;
    }

    @Test
    public void givenUserEmailThenReturnOptionalOfRegisteredUser() {
        Optional<RegisteredUser> userByEmail = authenticationRepository.getUserByEmail(EMAIL);
        assertThat(userByEmail).isNotEmpty();
        RegisteredUser registeredUser = userByEmail.get();
        assertThat(registeredUser.getPassword()).isEqualTo(PASSWORD);
        assertThat(registeredUser.getEmail()).isEqualTo(EMAIL);
    }

    @Test
    public void givenUserEmailWhenNotExistsThenReturnEmptyOptional() {
        Optional<RegisteredUser> userByEmail = authenticationRepository.getUserByEmail("invalid@test.com");
        assertThat(userByEmail).isEmpty();
    }
}