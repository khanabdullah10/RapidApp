package com.chetu.productManagementSystem.repositories;

import com.chetu.productManagementSystem.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product,Integer> {
}
