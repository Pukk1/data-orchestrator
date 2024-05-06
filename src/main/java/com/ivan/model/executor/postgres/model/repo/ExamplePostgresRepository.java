package com.ivan.model.executor.postgres.model.repo;

import com.ivan.model.executor.postgres.model.ExamplePostgresModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExamplePostgresRepository extends JpaRepository<ExamplePostgresModel, Long> {
}
