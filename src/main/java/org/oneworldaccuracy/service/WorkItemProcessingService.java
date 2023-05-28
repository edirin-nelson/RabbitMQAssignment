package org.oneworldaccuracy.service;

import org.oneworldaccuracy.model.WorkItem;
import org.oneworldaccuracy.repository.WorkItemRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class WorkItemProcessingService {
    private final WorkItemRepository workItemRepository;

    public WorkItemProcessingService(WorkItemRepository workItemRepository) {
        this.workItemRepository = workItemRepository;
    }



    @RabbitListener(queues = "work-item-queue")
    public void processWorkItem(WorkItem workItem) {
        // Calculate the square of the work item value
        int value = workItem.getValue();
        int result = value * value;

        // Simulate processing delay
        try {
            Thread.sleep(value * 10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Update the work item with the processing result
        workItem.setProcessed(true);
        workItem.setResult(result);
        workItemRepository.save(workItem);
    }
}

