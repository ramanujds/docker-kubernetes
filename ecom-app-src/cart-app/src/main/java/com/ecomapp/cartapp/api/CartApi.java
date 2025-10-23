package com.ecomapp.cartapp.api;

import com.ecomapp.cartapp.model.CartItem;
import com.ecomapp.cartapp.repository.CartRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/carts")
public class CartApi {

    private final CartRepository cartRepo;
    public CartApi(CartRepository cartRepo) {
        this.cartRepo = cartRepo;
    }

    @PostMapping
    public CartItem addToCart(@RequestBody CartItem cartItem) {
        if (cartRepo.existsById(cartItem.getId())){
            CartItem existingItem = cartRepo.findById(cartItem.getId()).orElse(null);
            if (existingItem != null) {
                existingItem.setQuantity(existingItem.getQuantity() + 1);
                existingItem.setTotalPrice(existingItem.getPrice() * existingItem.getQuantity());
                return cartRepo.save(existingItem);
            } else {
                return null; // or throw an exception
            }
        } else {
            cartItem.setQuantity(1);
            cartItem.setTotalPrice(cartItem.getPrice() * cartItem.getQuantity());
            return cartRepo.save(cartItem); // Add new item to the cart
        }
    }

    @GetMapping
    public List<CartItem> getCartItems() {
        return cartRepo.findAll(); // Retrieve all items in the cart
    }



}
