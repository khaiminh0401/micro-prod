package com.vnpt.prod.search.query;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.data.elasticsearch.core.suggest.response.Suggest;

import com.vnpt.prod.search.SearchFilters;

import co.elastic.clients.elasticsearch._types.SuggestMode;
import co.elastic.clients.elasticsearch._types.mapping.SuggestContext;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.search.CompletionSuggestOption;
import co.elastic.clients.elasticsearch.core.search.Suggestion;
import co.elastic.clients.elasticsearch.core.search.SuggestionBuilders;

public final class QueryBuilder {

    private QueryBuilder() {
    }

    public static SearchRequest buildSearchRequest(final SearchFilters filters, final SearchMeta meta) {
        SearchRequest.Builder builder = new SearchRequest.Builder();
        builder.index(meta.getIndex());

        Query.Builder queryBuilder = new Query.Builder();
        if (meta.getType() == QueryType.MATCH) {
            MatchQuery.Builder matchQuery = new MatchQuery.Builder();

            for (String field : meta.getFields()) {
                matchQuery.field(field);
            }
            matchQuery.query(filters.getTerm());
            queryBuilder.match(matchQuery.build());
        }else if (meta.getType() == QueryType.SUGGEST) {
            MatchQuery.Builder matchQuery = new MatchQuery.Builder();
            matchQuery.field("name").query(filters.getTerm()).analyzer("standard");
            queryBuilder.match(matchQuery.build());
        }

        builder.query(queryBuilder.build());

        return builder.build();
    }

}
