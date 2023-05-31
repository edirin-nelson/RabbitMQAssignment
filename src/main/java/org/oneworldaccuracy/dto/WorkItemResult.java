package org.oneworldaccuracy.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.URI;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WorkItemResult {
    private URI location;
    private WorkItemResponse response;
}
