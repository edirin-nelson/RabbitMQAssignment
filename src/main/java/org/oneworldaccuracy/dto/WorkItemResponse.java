package org.oneworldaccuracy.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WorkItemResponse {
    private String id;
    private int value;
    private boolean processed;
    private Integer result;

    public WorkItemResponse(String id) {
        this.id = id;
    }
}
