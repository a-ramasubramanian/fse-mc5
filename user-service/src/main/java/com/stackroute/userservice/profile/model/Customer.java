package com.stackroute.userservice.profile.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class Customer {
    @Id
    private String customerId;
    private String firstName;
    private String lastName;
    private String email;
    private Address address;
    private List<Order> orders;
}