package com.ecomapp.cartapp.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class CartItem {

    @Id
    private long id;
    private String name;
    private double price;
    private int quantity;
    private double totalPrice;

}
