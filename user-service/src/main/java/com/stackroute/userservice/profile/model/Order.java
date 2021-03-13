package com.stackroute.userservice.profile.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document
public class Order {
    @Id
    private String orderId;
    private String customerId;
    private List<CartProduct> cartProducts;
    private LocalDate billingDate;
    private Address billingAddress;
    private Address deliveryAddress;
    private String paymentDetails;
    private double totalBillAmount;
    private String status;
}
