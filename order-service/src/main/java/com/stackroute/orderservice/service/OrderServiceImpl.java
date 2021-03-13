package com.stackroute.orderservice.service;

import com.stackroute.orderservice.exception.OrderExistsException;
import com.stackroute.orderservice.exception.OrderNotFoundException;
import com.stackroute.orderservice.model.Order;
import com.stackroute.orderservice.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service class for managing orders
 */
@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    /**
     * Service method to add new order
     */
    @Override
    public Order addNewOrder(Order order) throws OrderExistsException {
        if (orderRepository.findById(order.getOrderId()).isPresent()) {
            throw new OrderExistsException();
        }
        return orderRepository.save(order);
    }

    /**
     * Service method to get order by order id
     */
    @Override
    public Order getOrderByOrderId(String orderId) throws OrderNotFoundException {
        return orderRepository.findById(orderId)
                .orElseThrow(OrderNotFoundException::new);
    }
}
