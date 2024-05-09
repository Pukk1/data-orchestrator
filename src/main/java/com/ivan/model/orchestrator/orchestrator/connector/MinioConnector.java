package com.ivan.model.orchestrator.orchestrator.connector;

import io.minio.MinioClient;
import org.modelmapper.ModelMapper;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class MinioConnector {
    private final MinioClient minioClient;
    private final ModelMapper modelMapper;

    public MinioConnector(MinioClient minioClient, ModelMapper modelMapper) {
        this.minioClient = minioClient;
        this.modelMapper = modelMapper;
    }

    public <SM, ID extends Number> SM save(SM splitEntity) {
        E entity = modelMapper.map(splitEntity, entityClass);
        entity = repo.save(entity);
        modelMapper.map(entity, splitEntity);
        return splitEntity;
    }

    public <SM, E, ID extends Number> SM findById(ID splitEntityId, MongoRepository<E, ID> repo, SM splitEntity) {
        Optional<E> entity = repo.findById(splitEntityId);
        if (entity.isEmpty()) {
            return null;
        } else {
            modelMapper.map(entity, splitEntity);
            return splitEntity;
        }
    }

    public <SM, E, ID> void deleteById(ID splitEntityId, MongoRepository<E, ID> repo) {
        repo.deleteById(splitEntityId);
    }
}
