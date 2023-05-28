package org.oneworldaccuracy.service;

import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.oneworldaccuracy.model.WorkItem;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class WorkItemProducerService {
    private final RabbitTemplate rabbitTemplate;
    private Queue queue;

    private static Logger logger = LogManager.getLogger(WorkItemProducerService.class.toString());

    public void enqueueWorkItem(WorkItem workItem) {
        rabbitTemplate.convertAndSend(queue.getName(), workItem);
        logger.info("Sending work item to the Queue : " + workItem);
    }
}
