package com.vnpt.prod.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.vnpt.prod.model.Product;

public interface ProductElasticRepository extends ElasticsearchRepository<Product, UUID> {

    List<Product> findByNameContainingIgnoreCase(String keyword);
    // This interface will inherit methods for CRUD operations from ElasticsearchRepository
    // Additional custom query methods can be defined here if needed

}
