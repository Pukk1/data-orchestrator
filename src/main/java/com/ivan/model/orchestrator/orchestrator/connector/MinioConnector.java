package com.ivan.model.orchestrator.orchestrator.connector;

import com.ivan.model.orchestrator.annatation.MinioObject;
import com.ivan.model.orchestrator.model.MinioVersions;
import com.ivan.model.orchestrator.model.repository.MinioVersionsRepository;
import io.minio.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.annotation.Id;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class MinioConnector {
    private final MinioClient minioClient;
    private final ModelMapper modelMapper;
    private final String bucket;
    private final MinioVersionsRepository minioVersionsRepository;

    public MinioConnector(MinioClient minioClient, ModelMapper modelMapper, @Value("${minio.bucket}") String bucket, MinioVersionsRepository minioVersionsRepository) {
        this.minioClient = minioClient;
        this.modelMapper = modelMapper;
        this.bucket = bucket;
        this.minioVersionsRepository = minioVersionsRepository;
    }

    private <SM> String findIdValue(SM splitEntity) {
        var splitEntityClass = splitEntity.getClass();
        var fields = splitEntityClass.getDeclaredFields();
        Arrays.stream(fields).toList().forEach(it -> it.setAccessible(true));
        var idField = Arrays.stream(fields).toList().stream()
                .filter(it -> it.getAnnotation(Id.class) != null)
                .findFirst()
                .orElseThrow(RuntimeException::new);
        try {
            return idField
                    .get(splitEntity)
                    .toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private <SM> Field[] findMinioStoredFields(Class<SM> splitEntityClass) {
        var fields = splitEntityClass.getDeclaredFields();
        Arrays.stream(fields).toList().forEach(it -> it.setAccessible(true));
        Field[] minioFields = Arrays.stream(fields).toList().stream()
                .filter(it -> it.getAnnotation(MinioObject.class) != null)
                .toArray(Field[]::new);
        return minioFields;
    }

    private Long nextVersion(String prefix) {
        List<Long> fieldIds = new ArrayList<>();
        var items = minioClient.listObjects(
                ListObjectsArgs
                        .builder()
                        .bucket(bucket)
                        .build()
        );
        items.forEach(it -> {
            String objectName;
            try {
                objectName = it.get().objectName();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            if (objectName.startsWith(prefix)) {
                fieldIds.add(Long.valueOf(objectName.replace(prefix, "")));
            }
        });
        if (fieldIds.isEmpty()) {
            return 1L;
        } else {
            return fieldIds.stream().max(Long::compare).get() + 1;
        }
    }

    private <SM> void storeMinioFields(SM splitEntity, String idValue) {
        var minioStoredField = findMinioStoredFields(splitEntity.getClass());
        for (var field : minioStoredField) {
            if (field.getType().getComponentType() != byte[].class.getComponentType()) {
                throw new RuntimeException();
            }
            try {
                byte[] value = (byte[]) field.get(splitEntity);
                var prefix = splitEntity.getClass().getName() + idValue + field.getName();
                var nextVersion = nextVersion(prefix);
                minioClient.putObject(
                        PutObjectArgs
                                .builder()
                                .bucket(bucket)
                                .object(prefix + nextVersion)
                                .stream(new ByteArrayInputStream(value), value.length, -1)
                                .build()
                );
                var existedMinioVersion = minioVersionsRepository.findById(prefix);
                existedMinioVersion.ifPresent(minioVersions -> registerTransactionSynchronisationCallback(prefix + minioVersions.getVersion()));
                minioVersionsRepository.save(new MinioVersions(prefix, nextVersion));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void registerTransactionSynchronisationCallback(String key) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                try {
                    minioClient.removeObject(
                            RemoveObjectArgs
                                    .builder()
                                    .bucket(bucket)
                                    .object(key)
                                    .build()
                    );
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                TransactionSynchronization.super.afterCommit();
            }
        });
    }

    @Transactional
    public <SM> SM save(SM splitEntity) {

        var idValue = findIdValue(splitEntity);
        storeMinioFields(splitEntity, idValue);

        return splitEntity;
    }

    @Transactional
    public <SM, E, ID extends Number> SM findById(ID splitEntityId, SM splitEntity) {
        var minioFields = findMinioStoredFields(splitEntity.getClass());
        try {
            for (var minioField : minioFields) {
                var prefix = splitEntity.getClass().getName() + splitEntityId + minioField.getName();
                var version = minioVersionsRepository.findById(prefix).get().getVersion();
                var bytes = minioClient.getObject(
                        GetObjectArgs
                                .builder()
                                .bucket(bucket)
                                .object(prefix + version)
                                .build()
                ).readAllBytes();
                minioField.set(splitEntity, bytes);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return splitEntity;
    }

    @Transactional
    public <SM, ID> void deleteById(ID splitEntityId, Class<SM> splitEntityClass) {
        var minioFields = findMinioStoredFields(splitEntityClass);
        try {
            for (var minioField : minioFields) {
                var prefix = splitEntityClass.getName() + splitEntityId + minioField.getName();
                var version = minioVersionsRepository.findById(prefix).get().getVersion();
                minioClient.removeObject(
                        RemoveObjectArgs
                                .builder()
                                .bucket(bucket)
                                .object(prefix + version)
                                .build()
                );
                minioVersionsRepository.deleteById(prefix);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
