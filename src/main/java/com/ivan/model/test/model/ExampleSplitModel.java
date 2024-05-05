package com.ivan.model.test.model;

import com.ivan.model.common.annatation.SplitEntity;
import com.ivan.model.mongo.model.ExampleMongoModel;
import com.ivan.model.postgres.model.ExamplePostgresModel;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

@SplitEntity(entity = ExamplePostgresModel.class, document = ExampleMongoModel.class)
public class ExampleSplitModel {

    public ExampleSplitModel(Long id, String postgresField, String mongoField) {
        this.id = id;
        this.postgresField = postgresField;
        this.mongoField = mongoField;
    }

    public ExampleSplitModel() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String postgresField;

    @Field
    private String mongoField;
}
