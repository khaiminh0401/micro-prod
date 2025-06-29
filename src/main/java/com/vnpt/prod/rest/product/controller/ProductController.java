package com.vnpt.prod.rest.product.controller;

import com.vnpt.prod.document.doc.DocumentPDF;
import com.vnpt.prod.model.ProductEntity;
import com.vnpt.prod.rest.document.dto.DocumentPDFResult;
import com.vnpt.prod.rest.product.dto.ProductDTO;
import com.vnpt.prod.search.SearchFilters;
import com.vnpt.prod.service.index.IndexService;
import com.vnpt.prod.service.product.ProductService;

import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/product")
@Tag(name = "Product", description = "Quản lý sản phẩm")
public class ProductController {

    private final ProductService service;
    private final IndexService indexService;

    public ProductController(
            ProductService service,
            IndexService indexService) {
        this.service = service;
        this.indexService = indexService;
    }

    // API tạo mới sản phẩm
    // @PostMapping
    // @Operation(summary = "Tạo mới sản phẩm", description = "Nhận thông tin sản
    // phẩm từ client và lưu vào cơ sở dữ liệu.")
    // public ProductEntity save(@RequestBody ProductEntity product) {
    // return service.save(product);
    // }

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

    @PostMapping("/suggest")
    @Operation(summary = "Gợi ý sản phẩm", description = "Gợi ý sản phẩm dựa trên các bộ lọc từ client.")
    public List<ProductDTO> suggest(@RequestBody final SearchFilters filters) {
        return service.suggest(filters);
    }

    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    @Operation(
        summary = "Upload file", 
        description = "Upload file để index vào Elasticsearch với attachment pipeline"
    )
    public ResponseEntity<String> uploadFile(
        @RequestParam("file") 
        @Schema(description = "File to upload", type = "string", format = "binary")
        MultipartFile file
    ) throws IOException {
        byte[] bytes = file.getBytes();
        String base64 = Base64.getEncoder().encodeToString(bytes);

        Map<String, Object> doc = new HashMap<>();
        doc.put("data", base64);
        doc.put("filename", file.getOriginalFilename());
        
        IndexRequest<Map<String, Object>> request = IndexRequest.of(i -> i
                .index("documents")
                .pipeline("attachment-pipeline")
                .document(doc));

        IndexResponse response = indexService.createIndex(request);
        return ResponseEntity.ok("Indexed: " + response.id());
    }

    @GetMapping("/search-content-file")
    public ResponseEntity<List<DocumentPDFResult>> searchContentFile(@RequestParam("q") String keyword) throws IOException {
        var results = service.searchDocument(keyword);

        return ResponseEntity.ok(results);
    }

}
