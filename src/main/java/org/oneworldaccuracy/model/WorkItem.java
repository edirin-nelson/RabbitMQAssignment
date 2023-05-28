package org.oneworldaccuracy.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "work_items")
public class WorkItem {
    @Id
    private String id;
    private int value;
    private boolean processed;
    private Integer result;
}
