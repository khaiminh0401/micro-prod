package com.vnpt.prod.repository.elastic;

import java.util.List;
import java.util.UUID;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.vnpt.prod.document.ProductDocument;

public interface ProductElasticRepository extends ElasticsearchRepository<ProductDocument, UUID> {

    List<ProductDocument> findByNameContainingIgnoreCase(String keyword);
    // This interface will inherit methods for CRUD operations from ElasticsearchRepository
    // Additional custom query methods can be defined here if needed

}
