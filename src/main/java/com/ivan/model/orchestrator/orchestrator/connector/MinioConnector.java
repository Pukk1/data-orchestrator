package com.ivan.model.orchestrator.orchestrator.connector;

import com.ivan.model.orchestrator.annatation.MinioObject;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.annotation.Id;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Field;
import java.util.Arrays;

@Component
public class MinioConnector {
    private final MinioClient minioClient;
    private final ModelMapper modelMapper;
    private final String bucket;

    public MinioConnector(MinioClient minioClient, ModelMapper modelMapper, @Value("${minio.bucket}") String bucket) {
        this.minioClient = minioClient;
        this.modelMapper = modelMapper;
        this.bucket = bucket;
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

    private <SM> void storeMinioFields(SM splitEntity, String idValue) {
        var minioStoredField = findMinioStoredFields(splitEntity.getClass());
        for (var field : minioStoredField) {
            if (field.getType().getComponentType() != byte[].class.getComponentType()) {
                throw new RuntimeException();
            }
            try {
                byte[] value = (byte[]) field.get(splitEntity);
                minioClient.putObject(
                        PutObjectArgs
                                .builder()
                                .bucket(bucket)
                                .object(idValue + field.getName())
                                .stream(new ByteArrayInputStream(value), value.length, -1)
                                .build()
                );
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public <SM> SM save(SM splitEntity) {

        var idValue = findIdValue(splitEntity);
        storeMinioFields(splitEntity, idValue);

        return splitEntity;
    }

    public <SM, E, ID extends Number> SM findById(ID splitEntityId, SM splitEntity) {
        var minioFields = findMinioStoredFields(splitEntity.getClass());
        try {
            for (var minioField : minioFields) {
                var bytes = minioClient.getObject(
                        GetObjectArgs
                                .builder()
                                .bucket(bucket)
                                .object(splitEntityId + minioField.getName())
                                .build()
                ).readAllBytes();
                minioField.set(splitEntity, bytes);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return splitEntity;
    }

    public <SM, ID> void deleteById(ID splitEntityId, Class<SM> splitEntityClass) {
        var minioFields = findMinioStoredFields(splitEntityClass);
        try {
            for (var minioField : minioFields) {
                minioClient.removeObject(
                        RemoveObjectArgs
                                .builder()
                                .bucket(bucket)
                                .object(splitEntityId + minioField.getName())
                                .build()
                );
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
