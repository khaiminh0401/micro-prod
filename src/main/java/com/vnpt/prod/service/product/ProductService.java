package com.vnpt.prod.service.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vnpt.prod.document.product.ProductDocument;
import com.vnpt.prod.document.product.ProductElasticRepository;
import com.vnpt.prod.helper.Indices;
import com.vnpt.prod.model.ProductEntity;
import com.vnpt.prod.repository.jpa.ProductRepository;
import com.vnpt.prod.rest.product.dto.ProductDTO;
import com.vnpt.prod.search.SearchFilters;
import com.vnpt.prod.search.query.ElasticsearchProxy;
import com.vnpt.prod.search.query.QueryType;
import com.vnpt.prod.search.query.SearchMeta;
import com.vnpt.prod.service.product.converter.ProductDTOConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ProductService {

    private final ProductRepository jpaRepo;
    private final ProductElasticRepository elasticRepo;
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Logger LOG = LoggerFactory.getLogger(ProductService.class);
    private final ElasticsearchProxy<ProductDocument, ProductDTO> client;
    private final ProductDTOConverter converter;

    public ProductService(
        ProductRepository jpaRepo,
        ProductElasticRepository elasticRepo,
        final ProductDTOConverter converter,
        ElasticsearchProxy<ProductDocument, ProductDTO> client) {
        this.jpaRepo = jpaRepo;
        this.elasticRepo = elasticRepo;
        this.converter = converter;
        this.client = client;
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
    public List<ProductDTO> search(SearchFilters filters) {
        return client.search(
                filters,
                new SearchMeta(List.of("name"), Indices.PRODUCT_INDEX, QueryType.MATCH),
                ProductDocument.class
        );
    }

    public void save(final ProductDTO dto) {
        final ProductDocument document = converter.convertToDocument(dto);
        if (document == null) {
            return;
        }

        this.elasticRepo.save(document);
    }

    public List<ProductDTO> suggest(final SearchFilters filters) {
        try {
            return this.client.suggest(filters, new SearchMeta(List.of("name"), Indices.PRODUCT_INDEX, QueryType.SUGGEST), ProductDocument.class);
        } catch (Exception e) {
            LOG.error("Error during suggest: {}", e.getMessage(), e);
            return List.of();
        }
    }

    public List<Map<String, Object>> searchDocument(String keyword) {
        try {
            return this.client.searchDocument(keyword);
        } catch (Exception e) {
            LOG.error("Error during searchDocument: {}", e.getMessage(), e);
            return List.of();
        }
    }
}
