package com.stackroute.userservice.profile.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stackroute.userservice.config.UserServiceConfiguration;
import com.stackroute.userservice.errorhandling.exception.CustomerExistsException;
import com.stackroute.userservice.errorhandling.exception.CustomerNotFoundException;
import com.stackroute.userservice.profile.dto.*;
import com.stackroute.userservice.profile.model.Address;
import com.stackroute.userservice.profile.model.CartProduct;
import com.stackroute.userservice.profile.model.Customer;
import com.stackroute.userservice.profile.model.Order;
import com.stackroute.userservice.profile.service.CustomerServiceImpl;
import org.junit.jupiter.api.AfterEach;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CustomerController.class)
@Import(UserServiceConfiguration.class)
public class CustomerControllerTests {

    public static final String API_BASE_URL = "/api/v1";
    public static final String EMAIL = "test@test.com";
    public static final String CUSTOMER_ID = "c1";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerServiceImpl customerService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ObjectMapper mapper;

    private NewCustomerDto newCustomerDto;
    private CustomerDto customerDto;
    private String customerJson;
    private Customer customer;
    private Customer newCustomer;

    @BeforeEach
    public void setUp() {
        AddressDto addressDto = new AddressDto("100", "street", "area", "city", "state", 10101, "name", 9999911111l);
        List<CartProductDto> productsDto = List.of(new CartProductDto("1", "Carrot", 10, 10));
        OrderDto orderDto = new OrderDto("o1", "c1", productsDto, LocalDate.now(), addressDto, addressDto, "creditcard", 100, "completed");

        Address address = new Address("100", "street", "area", "city", "state", 10101, "name", 9999911111l);
        List<CartProduct> products = List.of(new CartProduct("1", "Carrot", 10, 10));
        Order order = new Order("o1", "c1", products, LocalDate.now(), address, address, "creditcard", 100, "completed");
        newCustomer = new Customer(CUSTOMER_ID, "firstname", "lastname", EMAIL, address, new ArrayList<>());
        customer = new Customer(CUSTOMER_ID, "firstname", "lastname", EMAIL, address, List.of(order));

        newCustomerDto = new NewCustomerDto(CUSTOMER_ID, "firstname", "lastname", EMAIL, "password", addressDto);
        customerDto = new CustomerDto(CUSTOMER_ID, "firstname", "lastname", EMAIL, addressDto, List.of(orderDto));
    }

    @AfterEach
    public void tearDown() {
        newCustomerDto = null;
        customer = null;
        customerJson = null;
    }

    @Test
    public void givenCustomerJsonWhenCustomerRegisteredThenReturnCreatedStatus() throws Exception {
        when(customerService.addNewCustomer(any(Customer.class), anyString()))
                .thenReturn(newCustomer);
        MvcResult mvcResult = mockMvc.perform(
                post(API_BASE_URL + "/customer/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(newCustomerDto)))
                .andExpect(status().isCreated())
                .andReturn();

        CustomerDto registeredCustomer = toObjectFromJson(mvcResult, CustomerDto.class);
        assertThat(registeredCustomer)
                .isEqualToIgnoringGivenFields(newCustomerDto, "password", "orders");
        assertThat(registeredCustomer.getOrders()).isEmpty();
        verify(customerService, times(1))
                .addNewCustomer(any(Customer.class), anyString());

    }

    @Test
    public void givenCustomerJsonWhenCustomerAlreadyExistsThenReturnConflictStatus() throws Exception {
        when(customerService.addNewCustomer(any(Customer.class), anyString()))
                .thenThrow(new CustomerExistsException());
        MvcResult mvcResult = mockMvc.perform(
                post(API_BASE_URL + "/customer/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(newCustomerDto)))
                .andExpect(status().isConflict())
                .andReturn();
        verify(customerService, times(1))
                .addNewCustomer(any(Customer.class), anyString());

    }

    @Test
    public void givenCustomerIdThenReturnCustomerJson() throws Exception {
        when(customerService.getCustomerByCustomerId(anyString()))
                .thenReturn(customer);
        MvcResult mvcResult = mockMvc.perform(
                get(API_BASE_URL + "/customers/{customerID}", CUSTOMER_ID))
                .andExpect(status().isOk())
                .andReturn();

        CustomerDto foundCustomer = toObjectFromJson(mvcResult, CustomerDto.class);
        assertThat(foundCustomer).isEqualToComparingFieldByField(customerDto);
        verify(customerService, times(1))
                .getCustomerByCustomerId(anyString());
    }

    @Test
    public void givenCustomerIdWhenDoesNotExistThenReturnNotFoundStatus() throws Exception {
        when(customerService.getCustomerByCustomerId(anyString()))
                .thenThrow(new CustomerNotFoundException());
        MvcResult mvcResult = mockMvc.perform(
                get(API_BASE_URL + "/customers/{customerID}", CUSTOMER_ID))
                .andExpect(status().isNotFound())
                .andReturn();
        verify(customerService, times(1))
                .getCustomerByCustomerId(anyString());
    }


    private String toJson(final Object obj) throws JsonProcessingException {
        return mapper.writeValueAsString(obj);
    }

    private <T> T toObjectFromJson(MvcResult result, Class<T> toClass) throws Exception {
        return this.mapper.readValue(result.getResponse().getContentAsString(), toClass);
    }
}