package org.dev.tokengenerator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Form;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;

import java.util.Base64;

@ApplicationScoped
public class Tokengenerator {
    private static final String CLIENT_ID = "qmdyN9DJQp2e0Bj-NQhQqQ";
    private static final String CLIENT_SECRET = "_HjYgPslXe6behRB0AaPa35W7RBP8g";
    private static String accessToken = null;

    private String fetchAccessToken() {
        // If we already have a token, no need to fetch again.
        if (accessToken != null) {
            return accessToken;
        }

        String credentials = CLIENT_ID + ":" + CLIENT_SECRET;
        String tokenEndpoint = "https://www.reddit.com/api/v1/access_token";

        // Creating HTTP client to fetch the token
        Client client = ClientBuilder.newClient();
        Response response = client.target(tokenEndpoint)
                .request()
                .header(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes()))
                .post(Entity.form(new Form().param("grant_type", "client_credentials")));
        if (response.getStatus() == 200) {
            String tokenResponse = response.readEntity(String.class);
            ObjectMapper mapper = new ObjectMapper();
            try {
                JsonNode rootNode = mapper.readTree(tokenResponse);
                return rootNode.path("access_token").asText();
            } catch (Exception e) {
                throw new RuntimeException("Failed to extract access token", e);
            }

        }

        response.close();
        return accessToken;
    }

    public String getToken() {
        return fetchAccessToken();
    }
}
