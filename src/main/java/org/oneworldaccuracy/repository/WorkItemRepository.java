package org.oneworldaccuracy.repository;

import org.oneworldaccuracy.model.WorkItem;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkItemRepository extends MongoRepository<WorkItem, String> {
}

