package com.stackroute.orderservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartProduct {
    private String productId;
    private String productName;
    private int quantity;
    private double price;

}
