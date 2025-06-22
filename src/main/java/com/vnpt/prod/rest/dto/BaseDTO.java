package com.vnpt.prod.rest.dto;

import java.util.UUID;

public abstract class BaseDTO {
    private UUID id;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}
