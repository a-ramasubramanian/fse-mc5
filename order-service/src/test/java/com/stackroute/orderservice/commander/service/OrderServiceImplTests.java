package com.stackroute.orderservice.commander.service;

import com.stackroute.orderservice.exception.OrderExistsException;
import com.stackroute.orderservice.exception.OrderNotFoundException;
import com.stackroute.orderservice.model.Address;
import com.stackroute.orderservice.model.CartProduct;
import com.stackroute.orderservice.model.Order;
import com.stackroute.orderservice.repository.OrderRepository;
import com.stackroute.orderservice.service.OrderServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceImplTests {
    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Order order;
    private Address address;
    private List<CartProduct> products;
    private Optional<Order> emptyOptional;

    @BeforeEach
    public void setUp() {
        address = new Address("100", "street", "area", "city", "state", 10101, "name", 9999911111l);
        products = List.of(new CartProduct("1", "Carrot", 10, 10));
        order = new Order("o1", "1", products, LocalDate.now(), address, address, "creditcard", 100, "completed");
        emptyOptional = Optional.empty();

    }

    @AfterEach
    public void tearDown() {
        order = null;
        emptyOptional = null;
    }

    @Test
    public void givenOrderEntityWhenValidThenCreateNewOrder() throws OrderExistsException {
        when(orderRepository.findById("o1"))
                .thenReturn(emptyOptional);
        when(orderRepository.save(order))
                .thenReturn(order);
        Order addedOrder = orderService.addNewOrder(order);
        assertThat(addedOrder).isEqualTo(order);
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    public void givenOrderEntityWhenOrderExistsThenThrowException() throws OrderExistsException {
        when(orderRepository.findById("o1"))
                .thenReturn(Optional.of(order));
        assertThatThrownBy(() -> orderService.addNewOrder(order))
                .isInstanceOf(OrderExistsException.class);
        verify(orderRepository, times(1)).findById("o1");
    }

    @Test
    public void givenOrderIdWhenOrderExistsThenReturnOrderEntity() throws OrderNotFoundException {
        when(orderRepository.findById("o1")).thenReturn(Optional.of(order));
        Order existingOrder = orderService.getOrderByOrderId("o1");
        assertThat(existingOrder).isEqualTo(order);
        verify(orderRepository, times(1)).findById("o1");
    }

    @Test
    public void givenOrderIdWhenOrderDoesNotExistThenThrowException() throws OrderNotFoundException {
        when(orderRepository.findById("o1")).thenReturn(emptyOptional);
        assertThatThrownBy(() -> orderService.getOrderByOrderId("o1"))
                .isInstanceOf(OrderNotFoundException.class);
        verify(orderRepository, times(1)).findById("o1");
    }
}