package com.stackroute.userservice.profile.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartProductDto implements Serializable {
    private String productId;
    private String productName;
    private int quantity;
    private double price;

}
