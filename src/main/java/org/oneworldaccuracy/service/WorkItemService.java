package org.oneworldaccuracy.service;

import lombok.AllArgsConstructor;
import org.oneworldaccuracy.model.WorkItem;
import org.oneworldaccuracy.repository.WorkItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class WorkItemService {

    private WorkItemRepository workItemRepository;

    @Async
    public void processWorkItem(WorkItem workItem) {
        // Calculate the square of the work item value
        int value = workItem.getValue();
        int result = value * value;

        // Introduce a simulated delay
        try {
            Thread.sleep(value * 10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Update the work item with the processing result
        workItem.setResult(result);
        workItem.setProcessed(true);
        workItemRepository.save(workItem);
    }
}

