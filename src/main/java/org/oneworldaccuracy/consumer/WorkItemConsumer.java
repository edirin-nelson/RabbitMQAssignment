package org.oneworldaccuracy.consumer;

import lombok.AllArgsConstructor;
import org.oneworldaccuracy.model.WorkItem;
import org.oneworldaccuracy.service.WorkItemService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class WorkItemConsumer {

    private WorkItemService workItemService;

    @RabbitListener(queues = "work-items")
    public void processWorkItem(WorkItem workItem) {
        workItemService.processWorkItem(workItem);
    }
}

