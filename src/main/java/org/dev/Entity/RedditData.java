package org.dev.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RedditData {
    @JsonProperty("children")
    private List<RedditChildren> children;

    @JsonProperty("after")
    private String after;

    @JsonProperty("dist")
    private int dist;

    @JsonProperty("modhash")  // Add the missing field
    private String modhash;

    public List<RedditChildren> getChildren() {
        return children;
    }

    public void setChildren(List<RedditChildren> children) {
        this.children = children;
    }

    public String getAfter() {
        return after;
    }

    public void setAfter(String after) {
        this.after = after;
    }

    public int getDist() {
        return dist;
    }

    public void setDist(int dist) {
        this.dist = dist;
    }

    public String getModhash() {
        return modhash;
    }

    public void setModhash(String modhash) {
        this.modhash = modhash;
    }

    @Override
    public String toString() {
        return "RedditData{" +
                "children=" + children +
                ", after='" + after + '\'' +
                ", dist=" + dist +
                ", modhash='" + modhash + '\'' +
                '}';
    }
}
