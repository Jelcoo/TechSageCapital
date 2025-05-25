package com.techsage.banking.models.dto.responses;

import lombok.*;
import org.springframework.data.domain.*;

import java.util.*;

@Data
public class PageResponseDto<T> {
    private List<T> content;
    private int currentPage;
    private long totalRecords;
    private int recordsPerPage;
    private long recordsThisPage;
    private int totalPages;
    private List<Integer> previousPages = new ArrayList<>();
    private List<Integer> nextPages = new ArrayList<>();

    public PageResponseDto(Page<T> page) {
        this.content = page.getContent();
        this.currentPage = page.getNumber() + 1;
        this.totalRecords = page.getTotalElements();
        this.recordsPerPage = page.getSize();
        this.recordsThisPage = page.getNumberOfElements();
        this.totalPages = page.getTotalPages();

        for (int i = 2; i >= 1; i--) {
            if (currentPage - i >= 1) {
                previousPages.add(currentPage - i);
            }
        }

        for (int i = 1; i <= 2; i++) {
            if (currentPage + i <= totalPages) {
                nextPages.add(currentPage + i);
            }
        }
    }
}
