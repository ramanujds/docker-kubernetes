package com.ecomapp.productapp.api;

import com.ecomapp.productapp.model.Product;
import com.ecomapp.productapp.repository.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductRestController {

    private final ProductRepository productRepo;


    public ProductRestController(ProductRepository productRepo) {
        this.productRepo = productRepo;
    }

    @GetMapping
    public List<Product> getAllProducts() {
        return productRepo.findAll();
    }

    @GetMapping("/{id}")
    public Product getProductById(@PathVariable long id) {
        return productRepo.findById(id).orElse(null);
    }


    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public Product createProduct(Product product) {
        return productRepo.save(product);
    }

}
