package com.vnpt.prod.rest.document.dto;

import java.util.Map;

public class DocumentPDFResult {
    private Map<String, Object> document;
    private String highlightSnippet;

    public DocumentPDFResult(Map<String, Object> document, String highlightSnippet) {
        this.document = document;
        this.highlightSnippet = highlightSnippet;
    }

    public Map<String, Object> getDocument() {
        return document;
    }

    public String getHighlightSnippet() {
        return highlightSnippet;
    }
}
