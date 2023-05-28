package org.oneworldaccuracy.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class ReportResponse {
    private Map<Integer, Integer> itemCounts;
    private Map<Integer, Integer> processedCounts;
}
