package com.vnpt.prod.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableJpaRepositories(
    basePackages = "com.vnpt.prod.repository.jpa" // Package chứa các JPA repository
)
@EnableElasticsearchRepositories(
    basePackages = "com.vnpt.prod.repository.elastic" // Package chứa các Elasticsearch repository
)
public class RepositoryConfig {
    // Cấu hình bổ sung nếu cần
}
