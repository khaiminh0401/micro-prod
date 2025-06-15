package com.vnpt.prod.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import jakarta.persistence.Entity;

@Entity
@Document(indexName = "products") // Cho Elasticsearch
public class Product {

    @Id // JPA
    private UUID id;

    private String name;

    private String description;

    private BigDecimal price;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // constructor, getter/setter
}


