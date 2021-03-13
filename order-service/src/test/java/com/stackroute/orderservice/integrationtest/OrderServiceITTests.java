package com.stackroute.orderservice.integrationtest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stackroute.orderservice.OrderServiceApplication;
import com.stackroute.orderservice.dto.AddressDto;
import com.stackroute.orderservice.dto.CartProductDto;
import com.stackroute.orderservice.dto.OrderDto;
import com.stackroute.orderservice.model.Address;
import com.stackroute.orderservice.model.CartProduct;
import com.stackroute.orderservice.model.Order;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = OrderServiceApplication.class
)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestPropertySource(properties = "spring.data.mongodb.port=27020")
@EmbeddedKafka
@DirtiesContext
public class OrderServiceITTests {
    public static final String ORDERS_BASE_URL = "/api/v1/orders";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    private OrderDto orderDto;
    private Order order;
    private String orderJson;

    @BeforeEach
    public void setUp() throws JsonProcessingException {
        AddressDto addressDto = new AddressDto("100", "street", "area", "city", "state", 10101, "name", 9999911111l);
        List<CartProductDto> productsDto = List.of(new CartProductDto("1", "Carrot", 10, 10));
        orderDto = new OrderDto("o1", "1", productsDto, LocalDate.now(), addressDto, addressDto, "creditcard", 100, "completed");

        orderJson = toJson(orderDto);
        Address address = new Address("100", "street", "area", "city", "state", 10101, "name", 9999911111l);
        List<CartProduct> products = List.of(new CartProduct("1", "Carrot", 10, 10));
        order = new Order("o1", "1", products, LocalDate.now(), address, address, "creditcard", 100, "completed");


    }

    @AfterEach
    public void tearDown() {
        order = null;
        orderDto = null;
        orderJson = null;
    }

    @Test
    @org.junit.jupiter.api.Order(1)
    public void givenOrderDetailsThenCreateNewOrder() throws Exception {
        MvcResult mvcResult = mockMvc.perform(
                post(ORDERS_BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderJson))
                .andExpect(status().isCreated())
                .andReturn();
        OrderDto createdOrder = toObjectFromJson(mvcResult, OrderDto.class);
        assertThat(createdOrder).isEqualTo(orderDto);
    }

    @Test
    @org.junit.jupiter.api.Order(2)
    public void givenOrderDetailsWhenOrderExistsThenReturnConflictStatus() throws Exception {
        MvcResult mvcResult = mockMvc.perform(
                post(ORDERS_BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderJson))
                .andExpect(status().isConflict())
                .andReturn();
    }

    @Test
    @org.junit.jupiter.api.Order(3)
    public void givenOrderIdWhenOrderExistsThenReturnOrderJson() throws Exception {
        MvcResult mvcResult = mockMvc.perform(
                get(ORDERS_BASE_URL + "/{orderId}", "o1"))
                .andExpect(status().isOk())
                .andReturn();


        OrderDto orderResponse = toObjectFromJson(mvcResult, OrderDto.class);
        assertThat(orderResponse).isEqualTo(orderDto);
    }

    @Test
    public void givenOrderIdWhenOrderDoesNotExistThenReturnNotFound() throws Exception {
        mockMvc.perform(
                get(ORDERS_BASE_URL + "/{orderId}", "o4"))
                .andExpect(status().isNotFound());
    }

    private String toJson(final Object obj) throws JsonProcessingException {
        return mapper.writeValueAsString(obj);
    }

    private <T> T toObjectFromJson(MvcResult result, Class<T> toClass) throws Exception {
        return this.mapper.readValue(result.getResponse().getContentAsString(), toClass);
    }
}