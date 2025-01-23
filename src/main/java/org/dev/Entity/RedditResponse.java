package org.dev.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.bson.codecs.pojo.annotations.BsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true) // Ignore extra fields
public class RedditResponse {
    @BsonProperty("title")
    private String title;
    @BsonProperty("url")
    private String url;
    @BsonProperty("author")
    private String author;
    @BsonProperty("score")
    private int score;
    @BsonProperty("created_utc")
    @JsonProperty("created_utc")
    private long createdUtc;
    @BsonProperty("author_fullname")
    @JsonProperty("author_fullname")
    private String authorFullname;  // Added field
    @BsonProperty("subreddit")
    @JsonProperty("subreddit")
    private String subreddit;  // Added field
    @BsonProperty("selftext")
    @JsonProperty("selftext")
    private String selftext;  // Added field
    @BsonProperty("thumbnail")
    @JsonProperty("thumbnail")
    private String thumbnail;  // Added field

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public long getCreatedUtc() {
        return createdUtc;
    }

    public void setCreatedUtc(long createdUtc) {
        this.createdUtc = createdUtc;
    }

    public String getAuthorFullname() {
        return authorFullname;
    }

    public void setAuthorFullname(String authorFullname) {
        this.authorFullname = authorFullname;
    }

    public String getSubreddit() {
        return subreddit;
    }

    public void setSubreddit(String subreddit) {
        this.subreddit = subreddit;
    }

    public String getSelftext() {
        return selftext;
    }

    public void setSelftext(String selftext) {
        this.selftext = selftext;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    @Override
    public String toString() {
        return "RedditResponse{" +
                "title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", author='" + author + '\'' +
                ", score=" + score +
                ", createdUtc=" + createdUtc +
                ", authorFullname='" + authorFullname + '\'' +
                ", subreddit='" + subreddit + '\'' +
                ", selftext='" + selftext + '\'' +
                ", thumbnail='" + thumbnail + '\'' +
                '}';
    }
}
