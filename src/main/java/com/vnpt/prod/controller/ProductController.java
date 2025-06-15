package com.vnpt.prod.controller;

import com.vnpt.prod.model.Product;
import com.vnpt.prod.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    // API tạo mới sản phẩm
    @PostMapping
    @Operation(summary = "Tạo mới sản phẩm", description = "Nhận thông tin sản phẩm từ client và lưu vào cơ sở dữ liệu.")
    public Product save(@RequestBody Product product) {
        return service.save(product);
    }

    // API tìm kiếm (Elasticsearch)
    @GetMapping("/search")
    @Operation(summary = "Tìm kiếm sản phẩm", description = "Tìm kiếm sản phẩm dựa trên từ khóa sử dụng Elasticsearch.")
    public List<Product> search(@RequestParam String q) {
        return service.search(q);
    }

    // API đồng bộ dữ liệu PostgreSQL -> Elasticsearch
    @PostMapping("/sync")
    @Operation(summary = "Đồng bộ dữ liệu", description = "Đồng bộ dữ liệu từ PostgreSQL lên Elasticsearch.")
    public String syncAll() {
        service.syncAllToElasticsearch();
        return "Đã đồng bộ dữ liệu từ PostgreSQL lên Elasticsearch.";
    }

    // API lấy chi tiết theo ID
    @GetMapping("/{id}")
    @Operation(summary = "Lấy chi tiết sản phẩm", description = "Lấy thông tin chi tiết của sản phẩm dựa trên ID.")
    public Product getById(@PathVariable UUID id) {
        return service.getById(id);
    }
}
