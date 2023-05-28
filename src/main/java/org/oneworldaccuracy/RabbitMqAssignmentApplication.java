package org.oneworldaccuracy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories()
public class RabbitMqAssignmentApplication {

    public static void main(String[] args) {
        SpringApplication.run(RabbitMqAssignmentApplication.class, args);
    }

}
