package com.ivan.model.mongo.model.repo;

import com.ivan.model.mongo.model.ExampleMongoModel;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ExampleMongoRepository extends MongoRepository<ExampleMongoModel, Long> {
}
