package org.dev.OpenSeacrh;

import com.mongodb.client.MongoClients;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.http.HttpHost;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.opensearch.client.RestClient;
import org.opensearch.client.RestClientBuilder;
import org.opensearch.client.RestHighLevelClient;

@ApplicationScoped
public class OpenSearchClient {
    private RestHighLevelClient client;
    @ConfigProperty(name="opensearch.url")
    public String url;
    @ConfigProperty(name="opensearch.port")
    public int port;

    @PostConstruct
    void init() {
        RestClientBuilder builder = RestClient.builder(new HttpHost(url, port, "http"));
        RestHighLevelClient client = new RestHighLevelClient(builder);
        this.client = client;
    }

    public RestHighLevelClient getClient() {
        return client;
    }
}
