package com.vnpt.prod.search.query;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.vnpt.prod.document.AbstractDocument;
import com.vnpt.prod.rest.dto.BaseDTO;
import com.vnpt.prod.search.SearchFilters;
import com.vnpt.prod.search.converter.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ElasticsearchProxy<E extends AbstractDocument, T extends BaseDTO> {
    private static final Logger LOG = LoggerFactory.getLogger(ElasticsearchProxy.class);

    private final Map<Class<E>, Converter<E, T>> CONVERTER_MAP = new HashMap<>(10);
    private final ElasticsearchClient client;
    private final List<Converter<E, T>> converters;

    public ElasticsearchProxy(ElasticsearchClient client,
            List<Converter<E, T>> converters) {
        this.client = client;
        this.converters = converters;

        for (Converter<E, T> converter : converters) {
            CONVERTER_MAP.put(converter.getDocumentClass(), converter);
        }
    }

    public List<T> search(final SearchFilters filters, final SearchMeta meta, final Class<E> documentClass) {
        try {
            SearchResponse<E> response = client.search(
                    QueryBuilder.buildSearchRequest(filters, meta),
                    documentClass);

            List<E> documents = response.hits().hits().stream().map(Hit::source).toList();

            Converter<E, T> converter = CONVERTER_MAP.get(documentClass);

            return documents.stream().map(converter::convertToDto).toList();

        } catch (IOException e) {
            LOG.error("{}", e.getMessage(), e);
            return List.of();
        }
    }

    public List<T> suggest(final SearchFilters filters, final SearchMeta meta, final Class<E> documentClass) {
        try {
            SearchResponse<E> response = client.search(
                    QueryBuilder.buildSearchRequest(filters, meta),
                    documentClass);

            List<E> documents = response.hits().hits().stream().map(Hit::source).toList();

            Converter<E, T> converter = CONVERTER_MAP.get(documentClass);

            return documents.stream().map(converter::convertToDto).toList();

        } catch (IOException e) {
            LOG.error("{}", e.getMessage(), e);
            return List.of();
        }
    }

    public List<Map<String, Object>> searchDocument(String keyword) throws IOException {
            SearchRequest request = SearchRequest.of(s -> s
        .index("documents") // Tên index bạn đã push file vào
        .query(q -> q
            .match(m -> m
                .field("attachment.content")
                .query(keyword)
            )
        )
    );

    SearchResponse<Map> response = client.search(request, Map.class);

    List<Map<String, Object>> results = response.hits().hits().stream()
        .map(Hit::source)
        .collect(Collectors.toList());
        return results;
    }

}
