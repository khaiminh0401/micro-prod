package com.vnpt.prod.search.converter;

import com.vnpt.prod.document.AbstractDocument;
import com.vnpt.prod.rest.dto.BaseDTO;

public interface Converter<E extends AbstractDocument, T extends BaseDTO> {

    Class<E> getDocumentClass();

    T convertToDto(E document);

    E convertToDocument(T dto);
}
