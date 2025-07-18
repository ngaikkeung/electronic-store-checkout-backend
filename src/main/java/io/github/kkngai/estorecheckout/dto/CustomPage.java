package io.github.kkngai.estorecheckout.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomPage<T> {
    private List<T> content;
    private long totalElements;
    private int totalPages;
    private int pageSize;
    private int pageNumber;

    public CustomPage(Page<T> springPage) {
        this.content = springPage.getContent();
        this.totalPages = springPage.getTotalPages();
        this.totalElements = springPage.getTotalElements();
        this.pageSize = springPage.getSize();
        this.pageNumber = springPage.getNumber();
    }

}
