package org.dev.Kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.inject.Inject;
import org.dev.Entity.RedditResponse;
import org.dev.OpenSeacrh.OpenSearchIndexservice;
import org.dev.mongo.MongoDBService;
import org.eclipse.microprofile.reactive.messaging.Incoming;

public class kafkaPostConsumer {
    @Inject
    OpenSearchIndexservice openSearchIndexservice;
    @Inject
    private MongoDBService mongoDBService;
    @Incoming("reddit-posts-consumer")
    public void consume(String message) {
        try {
            RedditResponse postData = new ObjectMapper().readValue(message, RedditResponse.class);
            mongoDBService.savePostData(postData);
            openSearchIndexservice.indexDataIntoOpenSearch(message);
        } catch (Exception e) {
            System.err.println("Error consuming message: " + e.getMessage());
        }
    }
}
