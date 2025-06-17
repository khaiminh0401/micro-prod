package com.vnpt.prod.repository.jpa;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vnpt.prod.model.ProductEntity;

public interface ProductRepository extends JpaRepository<ProductEntity, UUID> {

}
