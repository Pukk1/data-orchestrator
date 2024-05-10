package com.ivan.model.executor.model;

import com.ivan.model.executor.mongo.model.repo.ExampleMongoRepository;
import com.ivan.model.executor.postgres.model.repo.ExamplePostgresRepository;
import com.ivan.model.orchestrator.annatation.MinioObject;
import com.ivan.model.orchestrator.annatation.SplitEntity;
import com.ivan.model.executor.mongo.model.ExampleMongoModel;
import com.ivan.model.executor.postgres.model.ExamplePostgresModel;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SplitEntity(
        entity = ExamplePostgresModel.class,
        entityHandlerRepository = ExamplePostgresRepository.class,
        document = ExampleMongoModel.class,
        documentHandlerRepository = ExampleMongoRepository.class
)
public class ExampleSplitModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String postgresText;

    @Field
    private String mongoText;

    @MinioObject
    private byte[] minioObject;
}
