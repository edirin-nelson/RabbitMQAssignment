package org.oneworldaccuracy.producer;

import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.oneworldaccuracy.model.WorkItem;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class WorkItemProducer {
    private RabbitTemplate rabbitTemplate;
    private Queue queue;

    private static Logger logger = LogManager.getLogger(WorkItemProducer.class.toString());

    public void sendWorkItem(WorkItem workItem) {
        rabbitTemplate.convertAndSend(queue.getName(), workItem);
        logger.info("Sending work item to the Queue : " + workItem);
    }
}

