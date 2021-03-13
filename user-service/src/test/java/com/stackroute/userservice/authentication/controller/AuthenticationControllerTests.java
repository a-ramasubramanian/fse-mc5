package com.stackroute.userservice.authentication.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stackroute.userservice.authentication.dto.AuthenticationResponseDto;
import com.stackroute.userservice.authentication.dto.RegisteredUserDto;
import com.stackroute.userservice.authentication.model.RegisteredUser;
import com.stackroute.userservice.authentication.security.JwtTokenUtil;
import com.stackroute.userservice.authentication.service.AuthenticationServiceImpl;
import com.stackroute.userservice.config.UserServiceConfiguration;
import com.stackroute.userservice.errorhandling.exception.InvalidCredentialsException;
import com.stackroute.userservice.errorhandling.exception.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthenticationController.class)
@Import(UserServiceConfiguration.class)
public class AuthenticationControllerTests {

    public static final String PASSWORD = "test";
    public static final String EMAIL = "test@test.com";
    public static final String BASE_URL = "/api/v1/users";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationServiceImpl authenticationService;

    @MockBean
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ObjectMapper mapper;

    private RegisteredUser user;
    private RegisteredUserDto regUser;

    @BeforeEach
    public void setUp() throws IOException {
        user = new RegisteredUser(1, EMAIL, PASSWORD);
        regUser = new RegisteredUserDto(EMAIL, PASSWORD);
    }

    @Test
    public void givenValidUserWhenAuthenticatedThenReturnToken() throws Exception {
        when(authenticationService.authenticateUser(any(RegisteredUser.class))).thenReturn(user);
        when(jwtTokenUtil.generateToken(anyInt())).thenReturn("mockToken");

        MvcResult mvcResult = mockMvc.perform(
                post(BASE_URL + "/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(regUser)))
                .andExpect(status().isOk())
                .andReturn();
        AuthenticationResponseDto responseDto = toObjectFromJson(mvcResult, AuthenticationResponseDto.class);
        assertThat(responseDto.getToken()).isNotNull().isNotEmpty().isEqualTo("mockToken");
        assertThat(responseDto.getEmail()).isEqualTo(EMAIL);

        verify(authenticationService, times(1))
                .authenticateUser(any(RegisteredUser.class));
    }

    @Test
    public void givenInvalidUserWhenAuthenticatedThenReturnNotFoundStatus() throws Exception {
        when(authenticationService.authenticateUser(any(RegisteredUser.class))).thenThrow(new UserNotFoundException());

        MvcResult mvcResult = mockMvc.perform(
                post(BASE_URL + "/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(regUser)))
                .andExpect(status().isNotFound())
                .andReturn();
        verify(authenticationService, times(1))
                .authenticateUser(any(RegisteredUser.class));
    }

    @Test
    public void givenInvalidUserCredentialsWhenAuthenticatedThenReturnUnAuthorizedStatus() throws Exception {
        when(authenticationService.authenticateUser(any(RegisteredUser.class))).thenThrow(new InvalidCredentialsException());

        MvcResult mvcResult = mockMvc.perform(
                post(BASE_URL + "/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(regUser)))
                .andExpect(status().isUnauthorized())
                .andReturn();
        verify(authenticationService, times(1))
                .authenticateUser(any(RegisteredUser.class));
    }

    private String toJson(final Object obj) throws JsonProcessingException {
        return mapper.writeValueAsString(obj);
    }

    private <T> T toObjectFromJson(MvcResult result, Class<T> toClass) throws Exception {
        return this.mapper.readValue(result.getResponse().getContentAsString(), toClass);
    }
}
