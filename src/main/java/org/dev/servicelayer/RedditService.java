package org.dev.servicelayer;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import org.dev.Entity.RedditChildren;
import org.dev.Entity.RedditData;
import org.dev.Entity.RedditPostresponse;
import org.dev.Entity.RedditResponse;

import org.bson.Document;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
    public RedditData maptoredditposts(List<RedditResponse> Documents)
    {
        RedditData data = new RedditData();
        List<RedditChildren> children = new ArrayList<>();

        for (RedditResponse doc : Documents) {
            RedditChildren child = new RedditChildren();

            RedditResponse response = new RedditResponse();
            response.setTitle(doc.getTitle());
            response.setUrl(doc.getUrl());
            response.setAuthor(doc.getAuthor());
            response.setScore(doc.getScore());
            response.setCreatedUtc(doc.getCreatedUtc());
            response.setAuthorFullname(doc.getAuthorFullname());
            response.setSubreddit(doc.getSubreddit());
            response.setSelftext(doc.getSelftext());
            response.setThumbnail(doc.getThumbnail());

            child.setData(response);
            children.add(child);
            data.setChildren(children);
        }
        return data;
    }

}
