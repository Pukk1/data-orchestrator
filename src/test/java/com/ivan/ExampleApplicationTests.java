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

    @PostConstruct
    public void onPostConstruct() {
        System.out.println("askjdlaksjd");
    }

    @Test
    @Transactional
    void testSave() {
		ExampleSplitModel exampleSplitModel = new ExampleSplitModel(6L, "test postgres", "test mongo");
		exampleSplitModelRepository.save(exampleSplitModel);
    }
}
