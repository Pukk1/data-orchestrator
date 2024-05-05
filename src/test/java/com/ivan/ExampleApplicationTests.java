package com.ivan;

import com.ivan.model.test.model.ExampleSplitModel;
import com.ivan.model.test.model.repo.ExampleSplitModelRepository;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ExampleApplicationTests {

    @Autowired
    private ExampleSplitModelRepository exampleSplitModelRepository;

    @PostConstruct
    public void onPostConstruct() {
        System.out.println("askjdlaksjd");
    }

    @Test
    void testSave() {
		ExampleSplitModel exampleSplitModel = new ExampleSplitModel(0L, "test postgres", "test mongo");
		exampleSplitModelRepository.save(exampleSplitModel);
    }

}
