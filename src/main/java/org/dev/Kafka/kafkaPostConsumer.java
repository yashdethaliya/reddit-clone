package org.dev.Kafka;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.context.propagation.TextMapGetter;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import org.dev.OpenSeacrh.OpenSearchIndexservice;
import org.dev.mongo.MongoDBService;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Incoming;

import java.util.Map;

public class kafkaPostConsumer {
    @ConfigProperty(name="telemetry.name")
    String telemetryName;
    private Tracer tracer;

    private static final TextMapGetter<Map<String, String>> TEXT_MAP_GETTER = new TextMapGetter<Map<String, String>>() {
        @Override
        public String get(Map<String, String> carrier, String key) {
            return carrier != null ? carrier.get(key) : null;
        }

        @Override
        public Iterable<String> keys(Map<String, String> carrier) {
            return carrier != null ? carrier.keySet() : null;
        }
    };
    @Inject
    OpenSearchIndexservice openSearchIndexservice;
    @Inject
    MongoDBService mongoDBService;
    @PostConstruct
    void init() {
        tracer = GlobalOpenTelemetry.getTracer(telemetryName);

    }
    @Incoming("reddit-posts-consumer")
    public void consume(String message,Map<String, String> headers) {
        try {
            Context parentContext = GlobalOpenTelemetry.getPropagators().getTextMapPropagator()
                    .extract(Context.current(), headers, TEXT_MAP_GETTER);
            Span kafkaChildConsumerSpan = tracer.spanBuilder("Message received from Kafka Consumer")
                    .setParent(parentContext)
                    .setAttribute("Received Post", message)
                    .startSpan();
            Context kafkaChildConsumerspanContext = Context.current().with(kafkaChildConsumerSpan);
            try(Scope kafkaconsumerscope=parentContext.with(kafkaChildConsumerSpan).makeCurrent()) {
                mongoDBService.savePostData(message,kafkaChildConsumerspanContext);
                openSearchIndexservice.indexDataIntoOpenSearch(message,kafkaChildConsumerspanContext);
            }
            finally {
                kafkaChildConsumerSpan.end();
            }
        } catch (Exception e) {
            System.err.println("Error consuming message: " + e.getMessage());
        }
    }
}
