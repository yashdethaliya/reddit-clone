package org.dev.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RedditChildren {
    @JsonProperty("data")
    private RedditResponse data;

    public RedditResponse getData() {
        return data;
    }

    public void setData(RedditResponse data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "RedditChildren{" +
                "data=" + data +
                '}';
    }
}
