package com.ecomapp.cartapp.repository;

import com.ecomapp.cartapp.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<CartItem,Long> {
}
