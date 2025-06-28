package com.vnpt.prod.document.doc;


import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import com.vnpt.prod.document.AbstractDocument;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
@Document(indexName = "documents")
public class DocumentPDF extends AbstractDocument {
    private String filename;

    @Field(type = FieldType.Object)
    private Attachment attachment;
}
