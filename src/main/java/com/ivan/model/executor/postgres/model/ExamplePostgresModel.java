package com.ivan.model.executor.postgres.model;

import com.ivan.model.executor.postgres.model.repo.ExamplePostgresRepository;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity(name = "ExamplePostgresModel")
public class ExamplePostgresModel {
    @Id
    private Long id;

    private String postgresText;
}
