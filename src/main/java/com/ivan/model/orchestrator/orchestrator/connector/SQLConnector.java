package com.ivan.model.orchestrator.orchestrator.connector;

import org.modelmapper.ModelMapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SQLConnector {

    private ModelMapper modelMapper;

    public SQLConnector(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public <SM, E, ID extends Number> SM save(SM splitEntity, JpaRepository<E, ID> repo, Class<E> entityClass) {
        E entity = modelMapper.map(splitEntity, entityClass);
        entity = repo.save(entity);
        SM splitModel = (SM) modelMapper.map(entity, splitEntity.getClass());
        return splitModel;
    }

    public <SM, E, ID extends Number> SM findById(ID splitEntityId, JpaRepository<E, ID> repo, Class<SM> splitEntityClass) {
        Optional<E> entity = repo.findById(splitEntityId);
        if (entity.isEmpty()) {
            return null;
        } else {
            return modelMapper.map(entity, splitEntityClass);
        }
    }

    public <SM, E, ID> void deleteById(ID splitEntityId, JpaRepository<E, ID> repo) {
        repo.deleteById(splitEntityId);
    }
}
