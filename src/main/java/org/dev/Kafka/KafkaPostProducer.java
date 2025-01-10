package org.dev.Kafka;


import jakarta.enterprise.context.ApplicationScoped;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.StringSerializer;
import org.dev.Entity.RedditChildren;
import org.dev.Entity.RedditPostresponse;

import java.util.Properties;

@ApplicationScoped
public class KafkaPostProducer {

    private final KafkaProducer<String, String> producer;
    private static final String TOPIC = "reddit-posts";

    public KafkaPostProducer() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        producer = new KafkaProducer<>(props);
    }
    public void sendPostsToKafka(RedditPostresponse response) {
        if (response != null && response.getPosts() != null) {
            for (RedditChildren child : response.getPosts()) {
                if (child != null && child.getData() != null) {
                    try {
                        String jsonPost = new ObjectMapper().writeValueAsString(child.getData());
                        producer.send(new ProducerRecord<>(TOPIC, null, jsonPost));
                        System.out.println("Sent post to Kafka: " + jsonPost);
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
