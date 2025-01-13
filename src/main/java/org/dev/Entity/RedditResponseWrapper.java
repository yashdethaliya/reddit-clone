package org.dev.Entity;

import org.opensearch.search.DocValueFormat;

public class RedditResponseWrapper {
    String _index;
    String _id;
    double _score;
    RedditResponse redditResponse;

    public RedditResponse getRedditResponse() {
        return redditResponse;
    }

    public void setRedditResponse(RedditResponse redditResponse) {
        this.redditResponse = redditResponse;
    }

    public String get_index() {
        return _index;
    }

    public void set_index(String _index) {
        this._index = _index;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public double get_score() {
        return _score;
    }

    public void set_score(double _score) {
        this._score = _score;
    }
}
