//package com.ivan.model.orchestrator.configuration.transaction.mongo;
//
//import com.mongodb.ConnectionString;
//import com.mongodb.client.MongoClient;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.mongodb.MongoDatabaseFactory;
//import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
//import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
//
//import static com.ivan.model.orchestrator.utils.TmpConsts.MY_PACKAGE;
//
//@Configuration
//@EnableMongoRepositories(basePackages = MY_PACKAGE)
//public class MongoTransactionManager extends AbstractMongoClientConfiguration {
//
//    private final String databaseName;
//
//    public MongoTransactionManager(@Value("${spring.data.mongodb.database}") String databaseName) {
//        this.databaseName = databaseName;
//    }
//
//    @Bean
//    org.springframework.data.mongodb.MongoTransactionManager transactionManager(MongoDatabaseFactory dbFactory) {
//        return new org.springframework.data.mongodb.MongoTransactionManager(dbFactory);
//    }
//
//    @Override
//    protected String getDatabaseName() {
//        return databaseName;
//    }
//
//    @Override
//    public MongoClient mongoClient() {
//        final ConnectionString connectionString = new ConnectionString();
//        final MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
//                .applyConnectionString(connectionString)
//                .build();
//        return MongoClients.create(mongoClientSettings);
//    }
//}