//package com.ivan.model.orchestrator.configuration.transaction.mongo;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.mongodb.MongoDatabaseFactory;
//import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
//
//@Configuration
//class MongoTransactionManager extends AbstractMongoClientConfiguration {
//
//    @Bean
//    MongoTransactionManager transactionManager(MongoDatabaseFactory dbFactory) {
//        return new MongoTransactionManager(dbFactory);
//    }
//
//    @Override
//    protected String getDatabaseName() {
//        return "";
//    }
//}
