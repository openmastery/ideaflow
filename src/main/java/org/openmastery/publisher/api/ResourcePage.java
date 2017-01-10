package org.openmastery.publisher.api;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

public class ResourcePage<T> extends PageImpl<T> {

    int totalPages;
    long totalElements;

    private static final long serialVersionUID = 3248189030448292002L;

    public ResourcePage(List<T> content, Pageable pageable, long total) {
        super(content, pageable, total);
    }

    public ResourcePage(List<T> content) {
        super(content);
    }

    public ResourcePage() {
        super(new ArrayList<T>());
    }

}
