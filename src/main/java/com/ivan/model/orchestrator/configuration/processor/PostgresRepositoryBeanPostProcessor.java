//package com.ivan.model.orchestrator.configuration.processor;
//
//import com.ivan.model.executor.postgres.model.repo.ExamplePostgresRepository;
//import com.ivan.model.orchestrator.configuration.SplitRepositoryInvocationHandler;
//import com.ivan.model.orchestrator.configuration.ThrowableMockInvocationHandler;
//import org.springframework.beans.BeansException;
//import org.springframework.beans.factory.config.BeanPostProcessor;
//import org.springframework.stereotype.Component;
//
//import java.lang.reflect.InvocationHandler;
//import java.lang.reflect.Method;
//import java.lang.reflect.Proxy;
//
//@Component
//public class PostgresRepositoryBeanPostProcessor implements BeanPostProcessor {
//
//    public ExamplePostgresRepository postgresRepository;
//
//    @Override
//    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
//        if (bean.getClass().getComponentType().equals(ExamplePostgresRepository.class)) {
//            postgresRepository = (ExamplePostgresRepository) bean;
//        }
//        return BeanPostProcessor.super.postProcessBeforeInitialization(bean, beanName);
//    }
//
//    @Override
//    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
//        if (bean.getClass().getComponentType().equals(ExamplePostgresRepository.class)) {
//            var implementation = Proxy.newProxyInstance(
//                    bean.getClass().getClassLoader(),
//                    new Class[]{bean.getClass()},
//                    new ThrowableMockInvocationHandler()
//            );
//            return implementation;
//        }
//        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
//    }
//}