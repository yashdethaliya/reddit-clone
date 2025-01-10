package org.dev.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.stream.Collectors;

public class RedditPostresponse {
    @JsonProperty("kind")
    private String kind;  // Add kind field

    @JsonProperty("data")
    private RedditData data;

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public RedditData getData() {
        return data;
    }

    public void setData(RedditData data) {
        this.data = data;
    }
    public List<RedditChildren> getPosts() {
        return data != null ? data.getChildren() : null;
    }
    public List<RedditResponse> getRedditResponses() {
        if (data != null && data.getChildren() != null) {
            return data.getChildren().stream()
                    .map(RedditChildren::getData)
                    .collect(Collectors.toList());
        }
        return null;
    }

    @Override
    public String toString() {
        return "RedditPostResponse{" +
                "kind='" + kind + '\'' +
                ", data=" + data +
                '}';
    }
}

