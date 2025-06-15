package com.vnpt.prod.service;

import com.vnpt.prod.model.Product;
import com.vnpt.prod.repository.ProductRepository;
import com.vnpt.prod.repository.ProductElasticRepository;
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
        List<Product> all = jpaRepo.findAll();
        elasticRepo.saveAll(all);
    }

    // Tìm kiếm từ Elasticsearch
    public List<Product> search(String keyword) {
        return elasticRepo.findByNameContainingIgnoreCase(keyword);
    }

    // Thêm mới sản phẩm (PostgreSQL + Elasticsearch)
    public Product save(Product p) {
        Product saved = jpaRepo.save(p);
        elasticRepo.save(saved);
        return saved;
    }

    // Lấy chi tiết từ DB
    public Product getById(UUID id) {
        return jpaRepo.findById(id).orElse(null);
    }
}
