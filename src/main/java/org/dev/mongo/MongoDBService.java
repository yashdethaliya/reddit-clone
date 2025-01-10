package org.dev.mongo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import jakarta.enterprise.context.ApplicationScoped;
import org.bson.Document;
import org.dev.Entity.RedditResponse;

@ApplicationScoped
public class MongoDBService {
    private static final MongoDatabase database=MongoClients.create("mongodb://localhost:27017").getDatabase("reddit");;


    public void savePostData(RedditResponse postData) throws JsonProcessingException {
        MongoCollection<Document> collection = database.getCollection("posts");
        String jsonString = new ObjectMapper().writeValueAsString(postData);
        Document doc = Document.parse(jsonString);
        collection.insertOne(doc);
    }


}
