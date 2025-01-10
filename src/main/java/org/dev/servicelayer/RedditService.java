package org.dev.servicelayer;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import org.dev.Entity.RedditPostresponse;
import java.io.IOException;

@ApplicationScoped
public class RedditService{
    public RedditPostresponse parseRedditResponse(String jsonResponse) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(jsonResponse, RedditPostresponse.class);
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse Reddit API response", e);
        }
    }
}
