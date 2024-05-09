package com.ivan.model.executor.model;

import com.ivan.model.executor.mongo.model.repo.ExampleMongoRepository;
import com.ivan.model.executor.postgres.model.repo.ExamplePostgresRepository;
import com.ivan.model.orchestrator.annatation.SplitEntity;
import com.ivan.model.executor.mongo.model.ExampleMongoModel;
import com.ivan.model.executor.postgres.model.ExamplePostgresModel;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@SplitEntity(
        entity = ExamplePostgresModel.class,
        entityHandlerRepository = ExamplePostgresRepository.class,
        document = ExampleMongoModel.class,
        documentHandlerRepository = ExampleMongoRepository.class
)
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
