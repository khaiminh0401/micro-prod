package com.vnpt.prod.rest.index;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vnpt.prod.service.index.IndexService;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/index")
@Tag(name = "Index", description = "Quản lý chỉ mục Elasticsearch")
public class IndexController {
    private final IndexService indexService;
    public IndexController(IndexService indexService) {
        this.indexService = indexService;
    }
    @RequestMapping("/create")
    public String createIndex() {
        indexService.createIndex(null);
        return "Index created successfully";
    }
}
