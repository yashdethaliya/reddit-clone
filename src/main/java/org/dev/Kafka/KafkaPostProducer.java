package org.dev.Kafka;


import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import io.opentelemetry.context.propagation.TextMapSetter;
import jakarta.enterprise.context.ApplicationScoped;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.dev.Entity.RedditChildren;
import org.dev.Entity.RedditPostresponse;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import io.opentelemetry.context.Context;

import java.util.HashMap;
import java.util.Map;


@ApplicationScoped
public class KafkaPostProducer {
    private static final Tracer tracer = GlobalOpenTelemetry.getTracer("reddit-service");
    @Channel("reddit-posts")
    private Emitter<String> emitter;
    private static final TextMapSetter<Map<String, String>> TEXT_MAP_SETTER = (carrier, key, value) -> {
        if (carrier != null) {
            carrier.put(key, value);
        }
    };
    public void sendPostsToKafka(RedditPostresponse response,Context parentcontext) {
        Span kafkachildproducerspan=tracer.spanBuilder("Message received to Kafka Producer")
                .setAttribute("Response from API", String.valueOf(response))
                .startSpan();
        try(Scope childScope = parentcontext.with(kafkachildproducerspan).makeCurrent()) {
            if (response != null && response.getPosts() != null) {
                for (RedditChildren child : response.getPosts()) {
                    if (child != null && child.getData() != null) {
                        try {
                            String jsonPost = new ObjectMapper().writeValueAsString(child.getData());
                            Map<String, String> headers = new HashMap<>();
                            GlobalOpenTelemetry.getPropagators().getTextMapPropagator()
                                    .inject(Context.current(), headers, TEXT_MAP_SETTER);
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
        finally {
            kafkachildproducerspan.end();
        }
    }
}
