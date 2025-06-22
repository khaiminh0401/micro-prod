package com.vnpt.prod.rest.product.controller;

import com.vnpt.prod.model.ProductEntity;
import com.vnpt.prod.rest.product.dto.ProductDTO;
import com.vnpt.prod.search.SearchFilters;
import com.vnpt.prod.service.product.ProductService;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    // API tạo mới sản phẩm
    @PostMapping
    @Operation(summary = "Tạo mới sản phẩm", description = "Nhận thông tin sản phẩm từ client và lưu vào cơ sở dữ liệu.")
    public ProductEntity save(@RequestBody ProductEntity product) {
        return service.save(product);
    }

    // API tìm kiếm (Elasticsearch)
    @PostMapping("/search")
    @Operation(summary = "Tìm kiếm sản phẩm", description = "Tìm kiếm sản phẩm dựa trên các bộ lọc từ client.")
    public List<ProductDTO> search(@RequestBody final SearchFilters filters) {
        return service.search(filters);
    }
    @PostMapping
    @Operation(summary = "Lưu sản phẩm", description = "Lưu thông tin sản phẩm từ client vào cơ sở dữ liệu.")
    public void save(@RequestBody final ProductDTO dto) {
        service.save(dto);
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
    public ProductEntity getById(@PathVariable UUID id) {
        return service.getById(id);
    }
}
