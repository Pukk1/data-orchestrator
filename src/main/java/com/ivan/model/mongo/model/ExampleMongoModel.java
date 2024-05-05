package com.ivan.model.mongo.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(value = "ExampleMongoModel")
public class ExampleMongoModel {
    @Id
    private Long id;

    private String mongoText;
}
