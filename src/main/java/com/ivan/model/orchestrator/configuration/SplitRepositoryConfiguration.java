package com.ivan.model.orchestrator.configuration;

import com.ivan.model.orchestrator.annatation.SplitEntity;
import com.ivan.model.orchestrator.orchestrator.connector.MinioConnector;
import com.ivan.model.orchestrator.orchestrator.connector.NoSQLConnector;
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
import org.springframework.data.mongodb.repository.MongoRepository;
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
    @Autowired
    private NoSQLConnector noSQLConnector;
    @Autowired
    private MinioConnector minioConnector;

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
            var sqlRepository = (JpaRepository) configurableBeanFactory.getBean(splitAnnotation.entityHandlerRepository());
            var document = splitAnnotation.document();
            var noSqlRepository = (MongoRepository) configurableBeanFactory.getBean(splitAnnotation.documentHandlerRepository());

            var implementation = Proxy.newProxyInstance(
                    repoClass.getClassLoader(),
                    new Class[]{repoClass},
                    new SplitRepositoryInvocationHandler(
                            sqlRepository,
                            noSqlRepository,
                            entity,
                            document,
                            sprlitEntityClass
                    )
            );

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

    public class SplitRepositoryInvocationHandler<SM, E, ME, ID extends Number> implements InvocationHandler {

        private JpaRepository<E, ID> sqlRepository;
        private MongoRepository<ME, ID> mongoRepository;
        private Class<E> sqlEntityClass;
        private Class<ME> noSqlEntityClass;
        private Class<SM> splitEntityClass;

        public SplitRepositoryInvocationHandler(JpaRepository<E, ID> sqlRepository, MongoRepository<ME, ID> mongoRepository, Class<E> sqlEntityClass, Class<ME> noSqlEntityClass, Class<SM> splitEntityClass) {
            this.sqlRepository = sqlRepository;
            this.mongoRepository = mongoRepository;
            this.sqlEntityClass = sqlEntityClass;
            this.noSqlEntityClass = noSqlEntityClass;
            this.splitEntityClass = splitEntityClass;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) {
            var methodName = method.getName();
            switch (methodName) {
                case "save" -> {
                    var splitEntity = (SM) args[0];
                    var res = sqlConnector.save(splitEntity, sqlRepository, sqlEntityClass);
                    res = noSQLConnector.save(res, mongoRepository, noSqlEntityClass);
                    return minioConnector.save(res);
                }
                case "findById" -> {
                    var id = (ID) args[0];
                    var res = sqlConnector.findById(id, sqlRepository, splitEntityClass);
                    res = noSQLConnector.findById(id, mongoRepository, res);
                    return minioConnector.findById(id, res);
                }
                case "deleteById" -> {
                    var id = (ID) args[0];
                    sqlConnector.deleteById(id, sqlRepository);
                    noSQLConnector.deleteById(id, mongoRepository);
                    minioConnector.deleteById(id, splitEntityClass);
                    return null;
                }
            }
            return null;
        }
    }
}
