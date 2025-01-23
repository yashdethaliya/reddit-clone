package org.dev.mongo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
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
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;
import org.dev.Entity.RedditResponse;
import org.dev.Entity.TrendingUser;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import java.util.*;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

@ApplicationScoped
public class MongoDBService {
    @ConfigProperty(name="telemetry.name")
    String telemetryName;
    private Tracer tracer;
    @ConfigProperty(name="mongo.url")
    private String mongoURL;
    @ConfigProperty(name="mongo.db")
    private String mongoDB;
    private MongoDatabase database;

    @PostConstruct
    void init() {

        CodecRegistry pojoCodecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));
        MongoClientSettings settings = MongoClientSettings.builder()
                .codecRegistry(pojoCodecRegistry)
                .applyConnectionString(new ConnectionString(mongoURL))
                .build();
        MongoClient mongoClient = MongoClients.create(settings);
        database = mongoClient.getDatabase(mongoDB);
        tracer = GlobalOpenTelemetry.getTracer(telemetryName);

    }
    public void savePostData(String postData, Context Parentcontext) throws JsonProcessingException {
        Span mongoSpan = tracer.spanBuilder("Message received to Mongo database")
                .startSpan();
        try(Scope mongoscope=Parentcontext.with(mongoSpan).makeCurrent()) {
            MongoCollection<RedditResponse> collection = database.getCollection("posts", RedditResponse.class);
            ObjectMapper objectMapper = new ObjectMapper();
            RedditResponse redditResponse = objectMapper.readValue(postData, RedditResponse.class);
            collection.insertOne(redditResponse);
        }
        finally {
            mongoSpan.end();
        }
    }

    public List<TrendingUser> finetredingusers(Context Parentcontext)
    {
        var mongoSpan = tracer.spanBuilder("Find Trending users from mongodb").startSpan();
        try(Scope mongoscope=Parentcontext.with(mongoSpan).makeCurrent()) {

            MongoCollection<RedditResponse> collection = database.getCollection("posts", RedditResponse.class);
            List<Bson> pipeline = Arrays.asList(
                    Aggregates.group("$author", Accumulators.sum("postCount", 1)),
                    Aggregates.sort(Sorts.descending("postCount"))
            );
            List<Document> aggregationResult = collection.aggregate(pipeline, Document.class).into(new ArrayList<>());
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
        MongoCollection<RedditResponse> collection = database.getCollection("posts", RedditResponse.class);
        return collection.find(new Document("author", username))
                .map(RedditResponse::getUrl)
                .into(new HashSet<>());
    }

    public List<RedditResponse> getpostsfromdb(int limit)
    {
        MongoCollection<RedditResponse> collection = database.getCollection("posts", RedditResponse.class);
        return collection.find().limit(limit).into(new ArrayList<>());

    }


}
