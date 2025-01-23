package org.dev.OpenSeacrh;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.http.HttpHost;
import org.apache.http.util.EntityUtils;
import org.apache.lucene.util.fst.PairOutputs;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.opensearch.client.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@ApplicationScoped
public class OpenSearchsearchservice {
    @ConfigProperty(name="telemetry.name")
    String telemetryName;
    private RestClient restClient;
    @ConfigProperty(name="opensearch.url")
    public String url;
    @ConfigProperty(name="opensearch.port")
    public int port;
    private Tracer tracer;
    @PostConstruct
    void init() {
        RestClientBuilder builder = RestClient.builder(new HttpHost(url, port, "http"));
        this.restClient = builder.build();
        tracer = GlobalOpenTelemetry.getTracer(telemetryName);
    }
    public String searchbykeyword(String index, String keyword, String value, int offset,int PAGE_SIZE, Context ParentContext) throws IOException {
        var opensearchspan = tracer.spanBuilder("Search-posts-from-opensearch").startSpan();
        try(Scope opensearchscope=ParentContext.with(opensearchspan).makeCurrent()) {
            String endpoint = "/" + index + "/_search";
            String query = "{\n" +
                    "  \"query\": {\n" +
                    "    \"match\": {\n" +
                    "      \"" + keyword + "\": \"" + value + "\"\n" +
                    "    }\n" +
                    "  },\n" +
                    "  \"from\": " + offset + ",\n" +
                    "  \"size\": " + PAGE_SIZE + "\n" +
                    "}";

            Request request = new Request("POST", endpoint);
            request.setJsonEntity(query);

            Response response = restClient.performRequest(request);
            if (response.getStatusLine().getStatusCode() == 404) {
                return "Index not Found";
            } else {
                return EntityUtils.toString(response.getEntity());
            }
        }
        finally {
            opensearchspan.end();
        }
    }

}
