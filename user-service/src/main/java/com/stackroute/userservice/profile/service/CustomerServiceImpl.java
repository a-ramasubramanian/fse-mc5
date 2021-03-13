package com.stackroute.userservice.profile.service;

import com.stackroute.userservice.authentication.model.RegisteredUser;
import com.stackroute.userservice.authentication.service.AuthenticationService;
import com.stackroute.userservice.errorhandling.exception.CustomerExistsException;
import com.stackroute.userservice.errorhandling.exception.CustomerNotFoundException;
import com.stackroute.userservice.profile.model.Customer;
import com.stackroute.userservice.profile.model.Order;
import com.stackroute.userservice.profile.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * Service class for managing customers
 */
@Service
public class CustomerServiceImpl implements CustomerService {

    public static final String CUSTOMER_NOT_FOUND = "Customer not found";
    public static final String CUSTOMER_EXISTS = "Customer with given email exists";
    private final CustomerRepository customerRepository;
    private final AuthenticationService authenticationService;

    @Autowired
    public CustomerServiceImpl(CustomerRepository customerRepository, AuthenticationService authenticationService) {
        this.customerRepository = customerRepository;
        this.authenticationService = authenticationService;
    }

    /**
     * Service method to add new customer
     */
    @Override
    public Customer addNewCustomer(Customer customer, String password) throws CustomerExistsException {
        if (customerRepository.findByEmail(customer.getEmail()).isPresent()) {
            throw new CustomerExistsException(CUSTOMER_EXISTS);
        }
        RegisteredUser newUser = new RegisteredUser(customer.getEmail(), password);

        authenticationService.saveUserCredentials(newUser);
        customer.setOrders(new ArrayList<>());
        return customerRepository.save(customer);
    }

    /**
     * Service method to update Order for a given customer
     */
    @Override
    public Customer updateCustomerOrder(Order order) throws CustomerNotFoundException {
        Customer customer = customerRepository.findById(order.getCustomerId())
                .orElseThrow(() -> new CustomerNotFoundException(CUSTOMER_NOT_FOUND));
        customer.getOrders().add(order);
        return customerRepository.save(customer);
    }

    /**
     * Service method to get customer using customer id
     */
    @Override
    public Customer getCustomerByCustomerId(String customerId) throws CustomerNotFoundException {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(CUSTOMER_NOT_FOUND));
    }
}
