package com.vnpt.prod.document.doc;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Attachment {
    private String content;
    private String content_type;
    private String language;
    private String author;
    // … thêm các field bạn cần
}
