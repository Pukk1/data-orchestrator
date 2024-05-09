package com.ivan.model.executor.mongo.model;

import com.ivan.model.executor.mongo.model.repo.ExampleMongoRepository;
import com.ivan.model.executor.postgres.model.repo.ExamplePostgresRepository;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(value = "ExampleMongoModel")
public class ExampleMongoModel {
    @Id
    private Long id;

    private String mongoText;
}
