package org.oneworldaccuracy.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReportResponse {
//    private Map<Integer, Integer> itemCounts;
//    private Map<Integer, Integer> processedCounts;
    private List<Integer> workItemValues;
    private List<Integer> itemCounts;
    private List<Integer> processedCounts;
}
