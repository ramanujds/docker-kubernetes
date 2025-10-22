package com.ecomapp.productapp.controller;

import com.ecomapp.productapp.dto.CartItem;
import com.ecomapp.productapp.model.Product;
import com.ecomapp.productapp.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;



@Controller
public class ProductViewController {

//    @Value("${CART_SERVICE_URL}")
//    private String cartServiceUrl;

    private final ProductRepository productRepo;
    private final RestTemplate restTemplate;

    public ProductViewController(ProductRepository productRepo, RestTemplate restTemplate) {
        this.productRepo = productRepo;
        this.restTemplate = restTemplate;
    }

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/products")
    public String showProducts(Model model) {
        List<Product> products = productRepo.findAll();
        model.addAttribute("products", products);
        return "product-list";
    }

    @GetMapping("add-to-cart/{id}")
    public String addToCart(@PathVariable("id") long id, Model model) {
        // Here you would typically call the Cart API to add the product to the cart
        // For simplicity, we will just return the product list again
        Product product = productRepo.findById(id).orElse(null);
        if (product == null) {
            model.addAttribute("message", "Product not found!");
            return "product-list";
        }
        // display a message if the cart-service is not available
        try {
        restTemplate.postForObject("http://cart-service/api/carts", product, Product.class);
        } catch (Exception e) {
            model.addAttribute("error", "Cart service is currently unavailable. Please try again later.");
            return "redirect:/view-cart";
        }

        model.addAttribute("message", "Product added to cart successfully!");
        return "redirect:/view-cart";
    }

    @GetMapping("/view-cart")
    public String viewCart(Model model) {

        try{
        CartItem[] cartItems = restTemplate.getForObject("http://cart-service/api/carts", CartItem[].class);
        List<CartItem> items = List.of(cartItems);
        model.addAttribute("cartItems", items);
        } catch (Exception e) {
            model.addAttribute("error", "Cart service is currently unavailable. Please try again later.");
            return "cart-view";
        }

        return "cart-view";


    }
}
