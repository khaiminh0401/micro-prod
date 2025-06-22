package com.vnpt.prod.rest.product.dto;

import com.vnpt.prod.rest.dto.BaseDTO;

public class ProductDTO extends BaseDTO {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
