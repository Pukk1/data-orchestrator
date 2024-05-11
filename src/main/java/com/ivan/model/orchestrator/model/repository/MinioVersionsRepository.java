package com.ivan.model.orchestrator.model.repository;

import com.ivan.model.orchestrator.model.MinioVersions;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MinioVersionsRepository extends JpaRepository<MinioVersions, String> {
}
