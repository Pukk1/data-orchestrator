package com.ivan.model.postgres.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity(name = "ExamplePostgresModel")
public class ExamplePostgresModel {
    @Id
    private Long id;

    private String postgresText;
}
