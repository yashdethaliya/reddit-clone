package org.dev.OpenSeacrh;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.http.HttpHost;
import org.apache.http.util.EntityUtils;
import org.apache.lucene.util.fst.PairOutputs;
import org.opensearch.client.Request;
import org.opensearch.client.Response;
import org.opensearch.client.RestClient;
import org.opensearch.client.RestClientBuilder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@ApplicationScoped
public class OpenSearchsearchservice {

    private final RestClient restClient;

    public OpenSearchsearchservice() {
        RestClientBuilder builder = RestClient.builder(new HttpHost("localhost", 9200, "http"));
        this.restClient = builder.build();
    }
    public String searchbykeyword(String index, String keyword, String value,int limit) throws IOException {
        String endpoint = "/" + index + "/_search";
        String query = "{ \"query\": { \"match\": { \"" + keyword + "\": \"" + value + "\" } }, \"size\": " + limit + " }";


        Request request = new Request("POST", endpoint);
        request.setJsonEntity(query);

        Response response = restClient.performRequest(request);
        if(response.getStatusLine().getStatusCode()==404)
        {
            return "Index not Found";
        }
        else {
            return EntityUtils.toString(response.getEntity());
        }
    }

}
