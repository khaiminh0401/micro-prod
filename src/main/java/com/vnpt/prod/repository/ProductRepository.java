package com.vnpt.prod.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vnpt.prod.model.Product;

public interface ProductRepository extends JpaRepository<Product, UUID> {

}
