package com.stackroute.userservice.profile.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document
public class OrderDto implements Serializable {
    private String orderId;
    private String customerId;
    private List<CartProductDto> cartProducts;
    private LocalDate billingDate;
    private AddressDto billingAddress;
    private AddressDto deliveryAddress;
    private String paymentDetails;
    private double totalBillAmount;
    private String status;
}
