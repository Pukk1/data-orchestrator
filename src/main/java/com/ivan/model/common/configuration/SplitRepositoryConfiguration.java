package com.ivan.model.common.configuration;

import com.ivan.model.common.repository.SplitRepository;
import com.ivan.model.test.model.repo.ExampleSplitModelRepository;
import jakarta.annotation.PostConstruct;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.stereotype.Component;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static com.ivan.model.common.utils.TmpConsts.MY_PACKAGE;

@Configuration
public class SplitRepositoryConfiguration implements BeanFactoryAware {

    @Autowired
    private ApplicationContext applicationContext;

    private BeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @PostConstruct
    public void onPostConstruct() {
        Reflections reflections = new Reflections(MY_PACKAGE, new SubTypesScanner(false));
        Set<Class> repoClasses = new HashSet<>(reflections.getSubTypesOf(SplitRepository.class));
        repoClasses = repoClasses.stream().filter(it -> it.getAnnotation(NoRepositoryBean.class) == null).collect(Collectors.toSet());

        ConfigurableBeanFactory configurableBeanFactory = (ConfigurableBeanFactory) beanFactory;
        for (Class repoClass : repoClasses) {
            Type[] genericTypes = Arrays.stream(repoClass.getGenericInterfaces())
                    .map(it -> (ParameterizedType) it)
                    .filter(it -> it.getRawType().getTypeName().equals(SplitRepository.class.getName()))
                    .findFirst()
                    .orElseThrow(RuntimeException::new)
                    .getActualTypeArguments();

            var implementation = Proxy.newProxyInstance(repoClass.getClassLoader(), new Class[] {repoClass}, new SplitRepositoryInvocationHandler());

            String beanName = repoClass.getName();
            Object bean = implementation;
            configurableBeanFactory.registerSingleton(beanName, bean);
        }
    }

    public class SplitRepositoryInvocationHandler implements InvocationHandler {

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            var methodName = method.getName();
            if (methodName.equals("save")) {

            } else if (methodName.equals("findById")) {

            }
            return null;
        }
    }
}
