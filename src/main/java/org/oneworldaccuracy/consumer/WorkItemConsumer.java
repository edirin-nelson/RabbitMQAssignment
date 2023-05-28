package org.oneworldaccuracy.consumer;

import lombok.AllArgsConstructor;
import org.oneworldaccuracy.model.WorkItem;
import org.oneworldaccuracy.service.WorkItemService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class WorkItemConsumer {

    private WorkItemService workItemService;
    @Value("${rabbitmq.queue}")
    private String queueName;

    @RabbitListener(queues = "${rabbitmq.queue}")
    public void processWorkItem(WorkItem workItem) {
        workItemService.processWorkItem(workItem);
    }
}

