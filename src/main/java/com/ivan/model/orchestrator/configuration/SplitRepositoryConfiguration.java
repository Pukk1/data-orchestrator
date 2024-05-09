package com.ivan.model.orchestrator.configuration;

import com.ivan.model.orchestrator.annatation.SplitEntity;
import com.ivan.model.orchestrator.orchestrator.connector.SQLConnector;
import com.ivan.model.orchestrator.repository.SplitRepository;
import jakarta.annotation.PostConstruct;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.ivan.model.orchestrator.utils.TmpConsts.MY_PACKAGE;

@Configuration
public class SplitRepositoryConfiguration implements BeanFactoryAware {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private SQLConnector sqlConnector;

    private ConfigurableBeanFactory configurableBeanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        this.configurableBeanFactory = (ConfigurableBeanFactory) beanFactory;
    }

    @PostConstruct
    public void registerSplitRepository() {
        var repoClasses = getSplitRepositoryClasses();

        for (Class repoClass : repoClasses) {
            var genericTypes = getSplitAnnotationData(repoClass);
            var sprlitEntityClass = genericTypes.get(0);
            var splitAnnotation = (SplitEntity) sprlitEntityClass.getAnnotation(SplitEntity.class);

            var entity = splitAnnotation.entity();
            var entityHandlerRepository = (JpaRepository) configurableBeanFactory.getBean(splitAnnotation.entityHandlerRepository());
            var document = splitAnnotation.document();
            var documentHandlerRepository = splitAnnotation.documentHandlerRepository();

            var implementation = Proxy.newProxyInstance(repoClass.getClassLoader(), new Class[]{repoClass}, new SplitRepositoryInvocationHandler(entityHandlerRepository, entity, sprlitEntityClass));

            String beanName = repoClass.getName();
            configurableBeanFactory.registerSingleton(beanName, implementation);
        }
    }

    private Set<Class<? extends SplitRepository<?, ?>>> getSplitRepositoryClasses() {
        Reflections reflections = new Reflections(MY_PACKAGE, new SubTypesScanner(false));
        Set<Class<? extends SplitRepository<?, ?>>> repoClasses = new HashSet(reflections.getSubTypesOf(SplitRepository.class));
        repoClasses = repoClasses.stream().filter(it -> it.getAnnotation(NoRepositoryBean.class) == null).collect(Collectors.toSet());
        return repoClasses;
    }

    private List<Class> getSplitAnnotationData(Class repoClass) {
        return Arrays.stream(Arrays.stream(repoClass.getGenericInterfaces())
                        .map(it -> (ParameterizedType) it)
                        .filter(it -> it.getRawType().getTypeName().equals(SplitRepository.class.getName()))
                        .findFirst()
                        .orElseThrow(RuntimeException::new)
                        .getActualTypeArguments())
                .map(it -> (Class) it)
                .collect(Collectors.toList());
    }

    public class SplitRepositoryInvocationHandler<SM, E, ID extends Number> implements InvocationHandler {

        private JpaRepository<E, ID> sqlRepository;
        private Class<E> sqlEntityClass;
        private Class<SM> splitEntity;

        public SplitRepositoryInvocationHandler(JpaRepository<E, ID> sqlRepository, Class<E> sqlEntityClass, Class<SM> splitEntityClass) {
            this.sqlRepository = sqlRepository;
            this.sqlEntityClass = sqlEntityClass;
            this.splitEntity = splitEntityClass;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) {
            var methodName = method.getName();
            switch (methodName) {
                case "save" -> {
                    return sqlConnector.save((SM)args[0], sqlRepository, sqlEntityClass);
                }
                case "findById" -> {
                    return sqlConnector.findById((ID) args[0], sqlRepository, splitEntity);
                }
                case "delete" -> {
                    sqlConnector.deleteById((ID) args[0], sqlRepository);
                    return null;
                }
            }
            return null;
        }
    }
}
