package org.oneworldaccuracy.producer;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.oneworldaccuracy.model.WorkItem;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class WorkItemProducer {
    private RabbitTemplate rabbitTemplate;
    private Queue queue;


    public void sendWorkItem(WorkItem workItem) {
        rabbitTemplate.convertAndSend(queue.getName(), workItem);
        log.info("Sending work item to the Queue : " + workItem);
    }
}

