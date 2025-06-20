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
            .map(p -> {
                ProductDocument doc = new ProductDocument();
                doc.setId(p.getId());
                doc.setName(p.getName());
                doc.setDescription(p.getDescription());
                doc.setPrice(p.getPrice());
                doc.setCreatedAt(p.getCreatedAt());
                doc.setUpdatedAt(p.getUpdatedAt());
                return doc;
            })
            .toList();
        elasticRepo.saveAll(all);
    }

    // Tìm kiếm từ Elasticsearch
    public List<ProductEntity> search(String keyword) {
        List<ProductDocument> docs = elasticRepo.searchByName(keyword);
        return elasticRepo.searchByName(keyword).stream()
            .map(doc -> {
                ProductEntity entity = new ProductEntity();
                entity.setId(doc.getId());
                entity.setName(doc.getName());
                entity.setDescription(doc.getDescription());
                entity.setPrice(doc.getPrice());
                entity.setCreatedAt(doc.getCreatedAt());
                entity.setUpdatedAt(doc.getUpdatedAt());
                return entity;
            })
            .toList();
    }

    // Thêm mới sản phẩm (PostgreSQL + Elasticsearch)
    public ProductEntity save(ProductEntity p) {
        ProductEntity saved = jpaRepo.save(p);
        ProductDocument doc = new ProductDocument();
        doc.setId(saved.getId());
        doc.setName(saved.getName());
        doc.setDescription(saved.getDescription());
        doc.setPrice(saved.getPrice());
        doc.setCreatedAt(saved.getCreatedAt());
        doc.setUpdatedAt(saved.getUpdatedAt());
        elasticRepo.save(doc);
        return saved;
    }

    // Lấy chi tiết từ DB
    public ProductEntity getById(UUID id) {
        return jpaRepo.findById(id).orElse(null);
    }
    
}
