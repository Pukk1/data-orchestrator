package com.ivan.model.common.configuration.processor;

import com.ivan.model.postgres.model.repo.ExamplePostgresRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

@Component
public class SplitRepositoryBeanPostProcessor implements BeanPostProcessor {

    @Autowired
    public ExamplePostgresRepository examplePostgresRepository;

    @PostConstruct
    public void doPostConstruct(){
        System.out.println("klsjadlkasjd");
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return BeanPostProcessor.super.postProcessBeforeInitialization(bean, beanName);
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }
}
