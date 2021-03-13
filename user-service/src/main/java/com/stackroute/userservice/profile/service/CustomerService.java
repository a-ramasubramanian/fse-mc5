package com.stackroute.userservice.profile.service;

import com.stackroute.userservice.errorhandling.exception.CustomerExistsException;
import com.stackroute.userservice.errorhandling.exception.CustomerNotFoundException;
import com.stackroute.userservice.profile.model.Customer;
import com.stackroute.userservice.profile.model.Order;

public interface CustomerService {

    Customer addNewCustomer(Customer customer, String password) throws CustomerExistsException;

    Customer updateCustomerOrder(Order order) throws CustomerNotFoundException;

    Customer getCustomerByCustomerId(String customerId) throws CustomerNotFoundException;
}
