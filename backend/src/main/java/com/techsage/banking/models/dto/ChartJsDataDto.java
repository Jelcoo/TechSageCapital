package com.techsage.banking.models.dto;

import lombok.*;

import java.util.*;

@Data
public class ChartJsDataDto {
    private List<String> labels = new ArrayList<>();
    private List<ChartDatasetDto> datasets = new ArrayList<>();

    public void addLabel(String label) {
        this.labels.add(label);
    }

    public void addDataset(ChartDatasetDto dataset) {
        this.datasets.add(dataset);
    }
}
