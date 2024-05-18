package com.ivan.model.orchestrator.configuration;

import com.ivan.model.orchestrator.orchestrator.connector.MinioConnector;
import com.ivan.model.orchestrator.orchestrator.connector.NoSQLConnector;
import com.ivan.model.orchestrator.orchestrator.connector.SQLConnector;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

@RequiredArgsConstructor
public class SplitRepositoryInvocationHandler<SM, E, ME, ID extends Number> implements InvocationHandler {

    private final SQLConnector sqlConnector;
    private final NoSQLConnector noSQLConnector;
    private final MinioConnector minioConnector;
    private final JpaRepository<E, ID> sqlRepository;
    private final MongoRepository<ME, ID> mongoRepository;
    private final Class<E> sqlEntityClass;
    private final Class<ME> noSqlEntityClass;
    private final Class<SM> splitEntityClass;

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