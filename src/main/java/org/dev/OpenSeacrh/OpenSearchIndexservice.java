package org.dev.OpenSeacrh;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.opensearch.action.index.IndexRequest;
import org.opensearch.client.RequestOptions;
import org.opensearch.client.RestHighLevelClient;
import org.opensearch.common.xcontent.XContentType;

@ApplicationScoped
public class OpenSearchIndexservice {
    @Inject
    OpenSearchClient openSearchClient;
    public void indexDataIntoOpenSearch(String jsonPost) {
        RestHighLevelClient client=openSearchClient.getClient();
        try {
            IndexRequest request = new IndexRequest("reddit-posts-index")
                    .source(jsonPost, XContentType.JSON); // Index the JSON data

            client.index(request, RequestOptions.DEFAULT);
            System.out.println("Indexed post into OpenSearch: " + jsonPost);
        } catch (Exception e) {
            System.err.println("Error indexing data into OpenSearch: " + e.getMessage());
        }
    }
}
