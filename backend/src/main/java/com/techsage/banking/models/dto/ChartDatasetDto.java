package com.techsage.banking.models.dto;

import com.fasterxml.jackson.annotation.*;
import lombok.*;

import java.util.*;

@Data
public class ChartDatasetDto {
    private String label;
    private List<Number> data = new ArrayList<>();

    @JsonProperty("yAxisID")
    private String yAxisID;

    public void addData(Number data) {
        this.data.add(data);
    }
}
