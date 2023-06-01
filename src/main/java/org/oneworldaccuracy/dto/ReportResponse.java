package org.oneworldaccuracy.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReportResponse {
    private List<Integer> workItemValues;
    private List<Integer> itemCounts;
    private List<Integer> processedCounts;
}
