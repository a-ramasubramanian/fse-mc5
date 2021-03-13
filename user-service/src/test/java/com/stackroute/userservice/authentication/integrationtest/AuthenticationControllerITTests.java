package com.stackroute.userservice.authentication.integrationtest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stackroute.userservice.UserServiceApplication;
import com.stackroute.userservice.authentication.dto.AuthenticationResponseDto;
import com.stackroute.userservice.authentication.dto.RegisteredUserDto;
import com.stackroute.userservice.authentication.model.RegisteredUser;
import com.stackroute.userservice.authentication.repository.AuthenticationRepository;
import com.stackroute.userservice.errorhandling.exception.InvalidCredentialsException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = UserServiceApplication.class
)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@EmbeddedKafka
@TestInstance(Lifecycle.PER_CLASS)
@DirtiesContext
public class AuthenticationControllerITTests {

    public static final String PASSWORD = "test";
    public static final String EMAIL = "test@test.com";
    public static final String BASE_URL = "/api/v1/users";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private AuthenticationRepository repository;

    private RegisteredUser user;
    private RegisteredUserDto regUser;

    @BeforeAll
    public void setUp() throws IOException {
        user = new RegisteredUser(1, EMAIL, BCrypt.hashpw(PASSWORD, BCrypt.gensalt()));
        regUser = new RegisteredUserDto(EMAIL, PASSWORD);
        repository.save(user);
    }

    @Test
    @Order(1)
    public void givenValidUserWhenAuthenticatedThenReturnToken() throws Exception {

        MvcResult mvcResult = mockMvc.perform(
                post(BASE_URL + "/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(regUser)))
                .andExpect(status().isOk())
                .andReturn();
        AuthenticationResponseDto responseDto = toObjectFromJson(mvcResult, AuthenticationResponseDto.class);
        assertThat(responseDto.getToken()).isNotNull().isNotEmpty();
        assertThat(responseDto.getEmail()).isEqualTo(EMAIL);
    }

    @Test
    @Order(2)
    public void givenInvalidUserWhenAuthenticatedThenReturnNotFoundStatus() throws Exception {
        MvcResult mvcResult = mockMvc.perform(
                post(BASE_URL + "/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(new RegisteredUserDto("invalid", "pass"))))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    @Order(3)
    public void givenInvalidUserCredentialsWhenAuthenticatedThenReturnUnAuthorizedStatus() throws Exception {

        MvcResult mvcResult = mockMvc.perform(
                post(BASE_URL + "/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(new RegisteredUserDto(EMAIL, "invalid"))))
                .andExpect(status().isUnauthorized())
                .andReturn();
    }

    private String toJson(final Object obj) throws JsonProcessingException {
        return mapper.writeValueAsString(obj);
    }

    private <T> T toObjectFromJson(MvcResult result, Class<T> toClass) throws Exception {
        return this.mapper.readValue(result.getResponse().getContentAsString(), toClass);
    }
}
