package com.stackroute.orderservice.service;

import com.stackroute.orderservice.exception.OrderExistsException;
import com.stackroute.orderservice.exception.OrderNotFoundException;
import com.stackroute.orderservice.model.Order;

public interface OrderService {
    Order addNewOrder(Order order) throws OrderExistsException;

    Order getOrderByOrderId(String orderId) throws OrderNotFoundException;
}
