package com.ivan;

import com.ivan.model.executor.model.ExampleSplitModel;
import com.ivan.model.executor.model.repo.ExampleSplitModelRepository;
import com.ivan.model.executor.mongo.model.ExampleMongoModel;
import com.ivan.model.executor.mongo.model.repo.ExampleMongoRepository;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
class ExampleApplicationTests {

    @Autowired
    private ExampleSplitModelRepository exampleSplitModelRepository;

    @Test
//    @Transactional
    void testSave() {
		ExampleSplitModel exampleSplitModel = new ExampleSplitModel(18L, "test postgres", "test mongo", new byte[]{1, 2});
		exampleSplitModelRepository.save(exampleSplitModel);
    }

    @Test
    void testFindByID() {
        var res = exampleSplitModelRepository.findById(18L);
        System.out.println(res);
    }

    @Test
    void testDeleteByID() {
       exampleSplitModelRepository.deleteById(18L);
    }
}
