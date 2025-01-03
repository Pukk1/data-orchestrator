package com.ivan;

import com.ivan.model.executor.model.ExampleSplitModel;
import com.ivan.model.executor.model.repo.ExampleSplitModelRepository;
import com.ivan.model.executor.mongo.model.repo.ExampleMongoRepository;
import com.ivan.model.executor.postgres.model.repo.ExamplePostgresRepository;
import io.minio.MinioClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@SpringBootTest
@TestInstance(value = TestInstance.Lifecycle.PER_CLASS)
class ExampleApplicationTests {

    @Autowired
    private ExampleSplitModelRepository exampleSplitModelRepository;
    @Autowired
    private ExamplePostgresRepository examplePostgresRepository;
    @Autowired
    private ExampleMongoRepository exampleMongoRepository;
    @Autowired
    private MinioClient minioClient;

    private final List<ExampleSplitModel> exampleSplitModels = new ArrayList<>();

    @Test
    void testSave() {
        for (var value : exampleSplitModels) {
            var result = exampleSplitModelRepository.save(value);
            Assertions.assertAll(
                    () -> Assertions.assertDoesNotThrow(
                            () -> {
                                if (!result.getId().equals(value.getId())) {
                                    throw new RuntimeException();
                                }
                            }
                    ),
                    () -> Assertions.assertDoesNotThrow(
                            () -> {
                                if (!result.getPostgresText().equals(value.getPostgresText())) {
                                    throw new RuntimeException();
                                }
                            }
                    ),
                    () -> Assertions.assertDoesNotThrow(
                            () -> {
                                if (!result.getMongoText().equals(value.getMongoText())) {
                                    throw new RuntimeException();
                                }
                            }
                    ),
                    () -> Assertions.assertDoesNotThrow(
                            () -> {
                                if (!Arrays.equals(result.getMinioObject(), value.getMinioObject())) {
                                    throw new RuntimeException();
                                }
                            }
                    )
            );
        }
    }

    @Test
    void testFindByID() {
//        for (ExampleSplitModel value : exampleSplitModels) {
        var value = exampleSplitModels.stream().findFirst().get();
        var result = exampleSplitModelRepository.findById(value.getId());
        Assertions.assertAll(
                () -> Assertions.assertDoesNotThrow(
                        () -> {
                            if (!result.getId().equals(value.getId())) {
                                throw new RuntimeException();
                            }
                        }
                )
//                    () -> Assertions.assertDoesNotThrow(
//                            () -> {
//                                if (!result.getPostgresText().equals(value.getPostgresText())) {
//                                    throw new RuntimeException();
//                                }
//                            }
//                    )
//                    () -> Assertions.assertDoesNotThrow(
//                            () -> {
//                                if (!result.getMongoText().equals(value.getMongoText())) {
//                                    throw new RuntimeException();
//                                }
//                            }
//                    ),
//                    () -> Assertions.assertDoesNotThrow(
//                            () -> {
//                                if (!Arrays.equals(result.getMinioObject(), value.getMinioObject())) {
//                                    throw new RuntimeException();
//                                }
//                            }
//                    )
        );
//        }
    }

    @Test
    void testDeleteByID() {
        for (ExampleSplitModel value : exampleSplitModels) {
            Assertions.assertDoesNotThrow(() -> exampleSplitModelRepository.deleteById(value.getId()));
        }
    }

    @BeforeAll
    public void getRandomLegalData() {
        for (int i = 0; i < 100; i++) {
            var model = new ExampleSplitModel(
                    new Random().nextLong(1, Long.MAX_VALUE),
                    "test postgres",
                    "test mongo",
                    new byte[]{1, 2, 3, 4}
            );
            exampleSplitModels.add(model);
        }
    }
}

//    @ParameterizedTest
//    @MethodSource("gen")
//    public void testTransactionWithThrownPostgresBySave() {
//
//    }
//
//    @ParameterizedTest
//    @MethodSource("gen")
//    public void testTransactionWithThrownPostgresByFind() {
//
//    }
//
//    @ParameterizedTest
//    @MethodSource("gen")
//    public void testTransactionWithThrownPostgresByDelete() {
//
//    }
//
//    @ParameterizedTest
//    @MethodSource("gen")
//    public void testTransactionWithThrownMongoBySave() {
//
//    }
//
//    @ParameterizedTest
//    @MethodSource("gen")
//    public void testTransactionWithThrownMongoByFind() {
//
//    }
//
//    @ParameterizedTest
//    @MethodSource("gen")
//    public void testTransactionWithThrownMongoByDelete() {
//
//    }
//
//    @ParameterizedTest
//    @MethodSource("gen")
//    public void testTransactionWithThrownMinioBySave() {
//
//    }
//
//    @ParameterizedTest
//    @MethodSource("gen")
//    public void testTransactionWithThrownMinioByFind() {
//
//    }
//
//    @ParameterizedTest
//    @MethodSource("gen")
//    public void testTransactionWithThrownMinioByDelete() {
//
//    }
//
//    private List<Integer> gen() {
//        List<Integer> result = new ArrayList<>();
//        for (int i = 0; i < 100; i++) {
//            result.add(i);
//        }
//        return result;
//    }
//}
