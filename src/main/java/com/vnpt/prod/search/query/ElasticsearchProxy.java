package com.vnpt.prod.search.query;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;

import com.vnpt.prod.document.AbstractDocument;
import com.vnpt.prod.rest.document.dto.DocumentPDFResult;
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
import java.util.Objects;
import java.util.Optional;
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

    public List<DocumentPDFResult> searchDocument(String keyword) throws IOException {
        // 1. Gửi request tìm kiếm
        SearchResponse<Map> response = client.search(
                SearchRequest.of(s -> s
                        .index("documents")
                        .query(q -> q
                                .match(m -> m
                                        .field("attachment.content")
                                        .query(keyword)))
                        .highlight(h -> {
                            h.fields("attachment.content", hf -> hf
                                    .preTags("<em>")
                                    .postTags("</em>"));
                            return h;
                        })),
                Map.class);
        // Sử dụng lambda để xử lý kết quả với null-safe handling
        return response.hits().hits().stream()
                .filter(hit -> Objects.nonNull(hit.source()))
                .map(hit -> {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> doc = (Map<String, Object>) hit.source();
                    
                    return Map.entry(hit, doc); // Pair hit và doc để giữ context
                })
                .filter(entry -> Objects.nonNull(entry.getValue()))
                .map(entry -> {
                    Hit<Map> hit = entry.getKey();
                    Map<String, Object> doc = entry.getValue();
                    
                    // Lấy tên file (nếu có) với lambda
                    Optional.ofNullable(doc.get("filename"))
                            .map(String::valueOf)
                            .ifPresent(filename -> System.out.println("Tên file: " + filename));
                    
                    // Lấy highlight với lambda và null-safe handling
                    String snippet = Optional.ofNullable(hit.highlight())
                            .map(highlights -> highlights.get("attachment.content"))
                            .filter(list -> !list.isEmpty())
                            .map(list -> list.get(0))
                            .orElse(null);
                    
                    Optional.ofNullable(snippet)
                            .ifPresent(s -> System.out.println("Đoạn liên quan: " + s));
                    
                    doc.remove("attachment"); // Loại bỏ trường attachment nếu không cần thiết
                    
                    return new DocumentPDFResult(doc, snippet);
                })
                .collect(Collectors.toList());
    }

}
