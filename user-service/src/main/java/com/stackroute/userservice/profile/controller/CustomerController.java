package com.stackroute.userservice.profile.controller;


import com.stackroute.userservice.errorhandling.exception.CustomerExistsException;
import com.stackroute.userservice.errorhandling.exception.CustomerNotFoundException;
import com.stackroute.userservice.profile.dto.CustomerDto;
import com.stackroute.userservice.profile.dto.NewCustomerDto;
import com.stackroute.userservice.profile.model.Customer;
import com.stackroute.userservice.profile.service.CustomerService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api")
public class CustomerController {

    private final CustomerService customerService;

    private final ModelMapper modelMapper;

    @Autowired
    public CustomerController(CustomerService customerService, ModelMapper modelMapper) {
        this.customerService = customerService;
        this.modelMapper = modelMapper;
    }

    /**
     * REST Endpoint for registering Customers
     * URI: /api/v1/customer/register  METHOD: POST
     * Response status: success: 201(CREATED) , Customer Already exists : 409(CONFLICT)
     */
    @PostMapping("v1/customer/register")
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerDto registerCustomer(@RequestBody NewCustomerDto customerDto) throws CustomerExistsException {
        Customer newCustomer = customerService.addNewCustomer(convertToEntity(customerDto), customerDto.getPassword());
        return convertToDto(newCustomer);
    }

    /**
     * REST Endpoint for fetching Customer details by customer id
     * URI: /api/v1/customers/{customerId}  METHOD: GET
     * Response status: success: 200(OK) , Customer Not found : 404(NOT FOUND)
     */
    @GetMapping("v1/customers/{customerId}")
    public CustomerDto getCustomerById(@PathVariable String customerId) throws CustomerNotFoundException {
        return convertToDto(customerService.getCustomerByCustomerId(customerId));
    }

    private CustomerDto convertToDto(Customer customer) {
        return modelMapper.map(customer, CustomerDto.class);
    }

    private Customer convertToEntity(NewCustomerDto customerDto) {
        return modelMapper.map(customerDto, Customer.class);
    }
}