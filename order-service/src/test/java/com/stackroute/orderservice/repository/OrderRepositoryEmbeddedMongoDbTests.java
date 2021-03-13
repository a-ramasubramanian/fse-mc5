package com.stackroute.orderservice.repository;

import com.stackroute.orderservice.model.Address;
import com.stackroute.orderservice.model.CartProduct;
import com.stackroute.orderservice.model.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@ExtendWith(SpringExtension.class)
@DirtiesContext
public class OrderRepositoryEmbeddedMongoDbTests {

    @Autowired
    private OrderRepository orderRepository;

    private Order order;

    @BeforeEach
    public void dataSetUp() {
        Address address = new Address("100", "street", "area", "city", "state", 10101, "name", 9999911111l);
        List<CartProduct> products = List.of(new CartProduct("1", "Carrot", 10, 10));
        order = new Order("o1", "1", products, LocalDate.now(), address, address, "creditcard", 100, "completed");

        orderRepository.save(order);
    }

    @Test
    public void givenOrderIdWhenOrderExistsThenReturnOrderEntity() {
        Optional<Order> optionalOrder = orderRepository.findById("o1");
        assertThat(optionalOrder)
                .isNotEmpty();
        Order orderO1 = optionalOrder.get();
        assertThat(orderO1)
                .isEqualTo(order);
    }

}