package com.stackroute.orderservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stackroute.orderservice.config.OrderServiceConfig;
import com.stackroute.orderservice.dto.AddressDto;
import com.stackroute.orderservice.dto.CartProductDto;
import com.stackroute.orderservice.dto.OrderDto;
import com.stackroute.orderservice.exception.OrderExistsException;
import com.stackroute.orderservice.exception.OrderNotFoundException;
import com.stackroute.orderservice.model.Address;
import com.stackroute.orderservice.model.CartProduct;
import com.stackroute.orderservice.model.Order;
import com.stackroute.orderservice.service.MessageProducerService;
import com.stackroute.orderservice.service.OrderService;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = OrderController.class)
@Import({OrderServiceConfig.class})
public class OrderControllerTests {
    public static final String ORDERS_BASE_URL = "/api/v1/orders";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @MockBean
    private MessageProducerService messageProducerService;

    @Autowired
    private ModelMapper modelMapper;

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
    public void givenOrderDetailsThenCreateNewOrder() throws Exception {
        when(orderService.addNewOrder(any(Order.class)))
                .thenReturn(order);
        MvcResult mvcResult = mockMvc.perform(
                post(ORDERS_BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderJson))
                .andExpect(status().isCreated())
                .andReturn();
        OrderDto createdOrder = toObjectFromJson(mvcResult, OrderDto.class);
        assertThat(createdOrder).isEqualTo(orderDto);
        verify(orderService, times(1))
                .addNewOrder(any(Order.class));
    }

    @Test
    public void givenOrderDetailsWhenOrderExistsThenReturnConflictStatus() throws Exception {
        when(orderService.addNewOrder(any(Order.class)))
                .thenThrow(new OrderExistsException());
        MvcResult mvcResult = mockMvc.perform(
                post(ORDERS_BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderJson))
                .andExpect(status().isConflict())
                .andReturn();
        verify(orderService, times(1))
                .addNewOrder(any(Order.class));
    }

    @Test
    public void givenOrderIdWhenOrderExistsThenReturnOrderJson() throws Exception {
        when(orderService.getOrderByOrderId(any(String.class)))
                .thenReturn(order);
        MvcResult mvcResult = mockMvc.perform(
                get(ORDERS_BASE_URL + "/{orderId}", "1"))
                .andExpect(status().isOk())
                .andReturn();


        OrderDto orderResponse = toObjectFromJson(mvcResult, OrderDto.class);
        assertThat(orderResponse).isEqualTo(orderDto);
        verify(orderService, times(1))
                .getOrderByOrderId(any(String.class));
    }

    @Test
    public void givenOrderIdWhenOrderDoesNotExistThenReturnNotFound() throws Exception {
        when(orderService.getOrderByOrderId(any(String.class)))
                .thenThrow(OrderNotFoundException.class);
        mockMvc.perform(
                get(ORDERS_BASE_URL + "/{orderId}", "1"))
                .andExpect(status().isNotFound());
        verify(orderService, times(1))
                .getOrderByOrderId(any(String.class));

    }


    private String toJson(final Object obj) throws JsonProcessingException {
        return mapper.writeValueAsString(obj);
    }

    private <T> T toObjectFromJson(MvcResult result, Class<T> toClass) throws Exception {
        return this.mapper.readValue(result.getResponse().getContentAsString(), toClass);
    }
}