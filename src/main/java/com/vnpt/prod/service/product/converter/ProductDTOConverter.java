package com.vnpt.prod.service.product.converter;

import org.springframework.stereotype.Component;

import com.vnpt.prod.document.product.ProductDocument;
import com.vnpt.prod.rest.product.dto.ProductDTO;
import com.vnpt.prod.search.converter.Converter;

@Component
public class ProductDTOConverter implements Converter<ProductDocument, ProductDTO> {

    @Override
    public Class<ProductDocument> getDocumentClass() {
        return ProductDocument.class;
    }

    @Override
    public ProductDTO convertToDto(final ProductDocument document) {
        if(document == null) {
            return null;
        }

        ProductDTO dto = new ProductDTO();
        dto.setId(document.getId());
        dto.setName(document.getName());

        return dto;
    }

    @Override
    public ProductDocument convertToDocument(final ProductDTO dto) {
        if (dto == null) {
            return null;
        }

        final ProductDocument ProductDocument = new ProductDocument();
        ProductDocument.setId(dto.getId());
        ProductDocument.setName(dto.getName());

        return ProductDocument;
    }
}