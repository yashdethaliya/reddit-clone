package org.dev.Kafka;


import jakarta.enterprise.context.ApplicationScoped;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.dev.Entity.RedditChildren;
import org.dev.Entity.RedditPostresponse;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import java.util.Properties;

@ApplicationScoped
public class KafkaPostProducer {

    @Channel("reddit-posts")
    private Emitter<String> emitter;
    public void sendPostsToKafka(RedditPostresponse response) {
        if (response != null && response.getPosts() != null) {
            for (RedditChildren child : response.getPosts()) {
                if (child != null && child.getData() != null) {
                    try {
                        String jsonPost = new ObjectMapper().writeValueAsString(child.getData());
                        emitter.send(jsonPost);
                    } catch (Exception e) {
                        System.err.println("Error sending post to Kafka: " + e.getMessage());
                    }
                }
            }
        } else {
            System.out.println("No posts available to send to Kafka.");
        }
    }
}
