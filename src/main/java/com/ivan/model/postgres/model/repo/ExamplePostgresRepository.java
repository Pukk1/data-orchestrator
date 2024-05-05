package com.ivan.model.postgres.model.repo;

import com.ivan.model.postgres.model.ExamplePostgresModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExamplePostgresRepository extends JpaRepository<ExamplePostgresModel, Long> {
}
