package com.ivan.model.executor.mongo.model.repo;

import com.ivan.model.executor.mongo.model.ExampleMongoModel;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ExampleMongoRepository extends MongoRepository<ExampleMongoModel, Long> {
}
