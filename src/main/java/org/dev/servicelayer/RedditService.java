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
    public RedditData maptoredditposts(List<Document> Documents)
    {
        RedditData data = new RedditData();
        List<RedditChildren> children = new ArrayList<>();

        for (Document doc : Documents) {
            RedditChildren child = new RedditChildren();

            RedditResponse response = new RedditResponse();
            response.setTitle(doc.getString("title"));
            response.setUrl(doc.getString("url"));
            response.setAuthor(doc.getString("author"));
            response.setScore(doc.getInteger("score", 0));
            response.setCreatedUtc(doc.getInteger("created_utc"));
            response.setAuthorFullname(doc.getString("author_fullname"));
            response.setSubreddit(doc.getString("subreddit"));
            response.setSelftext(doc.getString("selftext"));
            response.setThumbnail(doc.getString("thumbnail"));

            child.setData(response);
            children.add(child);
            data.setChildren(children);
        }
        return data;
    }

}
