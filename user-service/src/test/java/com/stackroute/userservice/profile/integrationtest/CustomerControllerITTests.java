package com.stackroute.userservice.profile.integrationtest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stackroute.userservice.UserServiceApplication;
import com.stackroute.userservice.profile.dto.*;
import com.stackroute.userservice.profile.model.Address;
import com.stackroute.userservice.profile.model.CartProduct;
import com.stackroute.userservice.profile.model.Customer;
import com.stackroute.userservice.profile.model.Order;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = UserServiceApplication.class
)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@EmbeddedKafka
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext
public class CustomerControllerITTests {

    public static final String API_BASE_URL = "/api/v1";
    public static final String EMAIL = "test@test.com";
    public static final String CUSTOMER_ID = "c1";
    public static final String NON_EXISTING_CUSTOMER_ID = "cc";

    @Autowired
    private MockMvc mockMvc;

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
    @org.junit.jupiter.api.Order(1)
    public void givenCustomerJsonWhenCustomerRegisteredThenReturnCreatedStatus() throws Exception {
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
    }

    @Test
    @org.junit.jupiter.api.Order(2)
    public void givenCustomerJsonWhenCustomerAlreadyExistsThenReturnConflictStatus() throws Exception {
        MvcResult mvcResult = mockMvc.perform(
                post(API_BASE_URL + "/customer/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(newCustomerDto)))
                .andExpect(status().isConflict())
                .andReturn();
    }

    @Test
    @org.junit.jupiter.api.Order(3)
    public void givenCustomerIdThenReturnCustomerJson() throws Exception {
        MvcResult mvcResult = mockMvc.perform(
                get(API_BASE_URL + "/customers/{customerID}", CUSTOMER_ID))
                .andExpect(status().isOk())
                .andReturn();

        CustomerDto foundCustomer = toObjectFromJson(mvcResult, CustomerDto.class);
        assertThat(foundCustomer).isEqualToIgnoringGivenFields(customerDto, "orders");
    }

    @Test
    @org.junit.jupiter.api.Order(4)
    public void givenCustomerIdWhenDoesNotExistThenReturnNotFoundStatus() throws Exception {
        MvcResult mvcResult = mockMvc.perform(
                get(API_BASE_URL + "/customers/{customerID}", NON_EXISTING_CUSTOMER_ID))
                .andExpect(status().isNotFound())
                .andReturn();
    }


    private String toJson(final Object obj) throws JsonProcessingException {
        return mapper.writeValueAsString(obj);
    }

    private <T> T toObjectFromJson(MvcResult result, Class<T> toClass) throws Exception {
        return this.mapper.readValue(result.getResponse().getContentAsString(), toClass);
    }
}