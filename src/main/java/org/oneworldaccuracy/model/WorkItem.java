package org.oneworldaccuracy.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(value = "work_items")
public class WorkItem {
    @Id
    private String id;
    private int value;
    private boolean processed;
    private Integer result;

    public WorkItem(int value) {
        this.value = value;
    }
}
