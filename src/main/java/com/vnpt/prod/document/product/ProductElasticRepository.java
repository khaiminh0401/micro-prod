package com.vnpt.prod.document.product;

import java.util.UUID;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ProductElasticRepository extends ElasticsearchRepository<ProductDocument, UUID> {

}
