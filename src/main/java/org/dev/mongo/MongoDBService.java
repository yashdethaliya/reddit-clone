package org.dev.mongo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Sorts;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import jakarta.enterprise.context.ApplicationScoped;
import org.bson.Document;
import org.dev.Entity.RedditResponse;
import org.dev.Entity.TrendingUser;

import java.util.*;

@ApplicationScoped
public class MongoDBService {
    private static final MongoDatabase database=MongoClients.create("mongodb://localhost:27017").getDatabase("reddit");
    private static final Tracer tracer = GlobalOpenTelemetry.getTracer("reddit-service");


    public void savePostData(RedditResponse postData, Context Parentcontext) throws JsonProcessingException {
        Span mongoSpan = tracer.spanBuilder("Message received to Mongo database")
                .startSpan();
        try(Scope mongoscope=Parentcontext.with(mongoSpan).makeCurrent()) {
            MongoCollection<Document> collection = database.getCollection("posts");
            String jsonString = new ObjectMapper().writeValueAsString(postData);
            Document doc = Document.parse(jsonString);
            collection.insertOne(doc);
        }
        finally {
            mongoSpan.end();
        }
    }

    public List<TrendingUser> finetredingusers(Context Parentcontext)
    {
        var mongoSpan = tracer.spanBuilder("Find Trending users from mongodb").startSpan();
        try(Scope mongoscope=Parentcontext.with(mongoSpan).makeCurrent()) {

            MongoCollection<Document> collection = database.getCollection("posts");
            List<Document> aggregationResult = collection.aggregate(
                    Arrays.asList(
                            Aggregates.group("$author", Accumulators.sum("postCount", 1)),
                            Aggregates.sort(Sorts.descending("postCount"))
                    )
            ).into(new ArrayList<>());

            List<TrendingUser> userPostCounts = new ArrayList<>();
            for (Document doc : aggregationResult) {
                String name = doc.getString("_id");
                int count = doc.getInteger("postCount");
                userPostCounts.add(new TrendingUser(name, count));
            }
            return userPostCounts;
        }
        finally {
            mongoSpan.end();
        }

    }
    public Set<String> getexistingURLs(String username)
    {
        MongoCollection<Document> collection = database.getCollection("posts");
        return collection.find(new Document("author", username))
                .map(doc -> doc.getString("url"))
                .into(new HashSet<>());
    }

    public List<Document> getpostsfromdb(int limit)
    {
           return database.getCollection("posts")
            .find().limit(limit)
            .into(new ArrayList<>());
    }


}
