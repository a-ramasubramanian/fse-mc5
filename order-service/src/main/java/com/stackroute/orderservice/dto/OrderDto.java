package com.stackroute.orderservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDto {
    private String orderId;
    private String customerId;
    private List<CartProductDto> cartProducts;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate billingDate;
    private AddressDto billingAddress;
    private AddressDto deliveryAddress;
    private String paymentDetails;
    private double totalBillAmount;
    private String status;
}
