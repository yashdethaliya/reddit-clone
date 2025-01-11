package org.dev.OpenSeacrh;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.http.HttpHost;
import org.opensearch.client.RestClient;
import org.opensearch.client.RestClientBuilder;
import org.opensearch.client.RestHighLevelClient;

@ApplicationScoped
public class OpenSearchClient {
    private final RestHighLevelClient client;

    public OpenSearchClient() {
        RestClientBuilder builder = RestClient.builder(new HttpHost("localhost", 9200, "http"));
        RestHighLevelClient client = new RestHighLevelClient(builder);
        this.client = client;
    }

    public RestHighLevelClient getClient() {
        return client;
    }
}
