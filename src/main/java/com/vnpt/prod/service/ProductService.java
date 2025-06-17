package com.vnpt.prod.service;

import com.vnpt.prod.document.ProductDocument;
import com.vnpt.prod.model.ProductEntity;
import com.vnpt.prod.repository.elastic.ProductElasticRepository;
import com.vnpt.prod.repository.jpa.ProductRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ProductService {

    private final ProductRepository jpaRepo;
    private final ProductElasticRepository elasticRepo;

    public ProductService(ProductRepository jpaRepo, ProductElasticRepository elasticRepo) {
        this.jpaRepo = jpaRepo;
        this.elasticRepo = elasticRepo;
    }

    // Đẩy toàn bộ dữ liệu từ PostgreSQL sang Elasticsearch
    public void syncAllToElasticsearch() {
        List<ProductEntity> products = jpaRepo.findAll();
        if (products.isEmpty()) {
            return; // Không có dữ liệu để đồng bộ
        }
        List<ProductDocument> all = jpaRepo.findAll().stream()
            .map(p -> new ProductDocument(
                p.getId(),
                p.getName(),
                p.getDescription(),
                p.getPrice(),
                p.getCreatedAt(),
                p.getUpdatedAt()))
            .toList();
        elasticRepo.saveAll(all);
    }

    // Tìm kiếm từ Elasticsearch
    public List<ProductEntity> search(String keyword) {
        List<ProductDocument> docs = elasticRepo.findByNameContainingIgnoreCase(keyword);
        return elasticRepo.findByNameContainingIgnoreCase(keyword).stream()
            .map(doc -> new ProductEntity(
                doc.getId(),
                doc.getName(),
                doc.getDescription(),
                doc.getPrice(),
                doc.getCreatedAt(),
                doc.getUpdatedAt()))
            .toList();
    }

    // Thêm mới sản phẩm (PostgreSQL + Elasticsearch)
    public ProductEntity save(ProductEntity p) {
        ProductEntity saved = jpaRepo.save(p);
        ProductDocument doc = new ProductDocument(
            saved.getId(),
            saved.getName(),
            saved.getDescription(),
            saved.getPrice(),
            saved.getCreatedAt(),
            saved.getUpdatedAt());
        elasticRepo.save(doc);
        return saved;
    }

    // Lấy chi tiết từ DB
    public ProductEntity getById(UUID id) {
        return jpaRepo.findById(id).orElse(null);
    }
}
