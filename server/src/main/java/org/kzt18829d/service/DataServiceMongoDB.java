package org.kzt18829d.service;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.kzt18829d.core.ports.DataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataServiceMongoDB implements DataService {
    private static final Logger log = LoggerFactory.getLogger(DataServiceMongoDB.class);
    String uri = "mongodb://localhost:3038/";


    private void test() {
        try(MongoClient mongoClient = MongoClients.create(uri)) {
            MongoDatabase database = mongoClient.getDatabase("bankAccounts");
            MongoCollection<Document> collection = database.getCollection("atm_BankAccounts");

        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

}
