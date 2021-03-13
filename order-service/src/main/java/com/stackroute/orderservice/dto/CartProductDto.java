package com.stackroute.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartProductDto {
    private String productId;
    private String productName;
    private int quantity;
    private double price;

}
