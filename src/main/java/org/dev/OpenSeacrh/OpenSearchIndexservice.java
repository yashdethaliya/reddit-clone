package org.dev.OpenSeacrh;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.opensearch.action.index.IndexRequest;
import org.opensearch.client.RequestOptions;
import org.opensearch.client.RestHighLevelClient;
import org.opensearch.client.indices.CreateIndexRequest;
import org.opensearch.client.indices.CreateIndexResponse;
import org.opensearch.client.indices.GetIndexRequest;
import org.opensearch.common.xcontent.XContentType;

import java.util.HashMap;

@ApplicationScoped
public class OpenSearchIndexservice {
    private static final Tracer tracer = GlobalOpenTelemetry.getTracer("reddit-service");
    @Inject
    OpenSearchClient openSearchClient;
    public void indexDataIntoOpenSearch(String jsonPost, Context Parentcontext) {
        Span Opensearchspan=tracer.spanBuilder("Message received to Opensearch to perform indexing")
                .startSpan();
        try(Scope Opensearchscope=Parentcontext.with(Opensearchspan).makeCurrent()) {
            RestHighLevelClient client = openSearchClient.getClient();
            try {
                GetIndexRequest getIndexRequest = new GetIndexRequest("reddit-posts-index");
                boolean exists = client.indices().exists(getIndexRequest, RequestOptions.DEFAULT);
                if (!exists) {
                    CreateIndexRequest createIndexRequest = new CreateIndexRequest("reddit-posts-index");
                    HashMap<String, Object> properties = new HashMap<>();
                    HashMap<String, String> authormapping = new HashMap<String, String>();
                    authormapping.put("type", "text");
                    properties.put("author", authormapping);
                    HashMap<String, String> author_fullnameMapping = new HashMap<String, String>();
                    author_fullnameMapping.put("type", "text");
                    properties.put("author_fullname", author_fullnameMapping);
                    HashMap<String, String> created_utcmapping = new HashMap<String, String>();
                    created_utcmapping.put("type", "long");
                    properties.put("created_utc", created_utcmapping);
                    HashMap<String, String> scoremapping = new HashMap<String, String>();
                    scoremapping.put("type", "integer");
                    properties.put("score", scoremapping);
                    HashMap<String, String> selftextmapping = new HashMap<String, String>();
                    selftextmapping.put("type", "text");
                    properties.put("selftext", selftextmapping);
                    HashMap<String, String> subredditmapping = new HashMap<String, String>();
                    subredditmapping.put("type", "text");
                    properties.put("subreddit", subredditmapping);
                    HashMap<String, String> thumbnailmapping = new HashMap<String, String>();
                    thumbnailmapping.put("type", "text");
                    properties.put("thumbnail", thumbnailmapping);
                    HashMap<String, String> titlemapping = new HashMap<String, String>();
                    titlemapping.put("type", "text");
                    properties.put("title", titlemapping);
                    HashMap<String, String> urlmapping = new HashMap<String, String>();
                    urlmapping.put("type", "text");
                    properties.put("url", urlmapping);
                    HashMap<String, Object> mappings = new HashMap<>();
                    mappings.put("properties", properties);
                    createIndexRequest.mapping(mappings);

                    CreateIndexResponse createIndexResponse = client.indices().create(createIndexRequest, RequestOptions.DEFAULT);
                    System.out.println(createIndexResponse);
                }
                IndexRequest request = new IndexRequest("reddit-posts-index")
                        .source(jsonPost, XContentType.JSON);
                client.index(request, RequestOptions.DEFAULT);
            } catch (Exception e) {
                System.err.println("Error indexing data into OpenSearch: " + e.getMessage());
            }
        }
        finally {
            Opensearchspan.end();
        }
    }
}
