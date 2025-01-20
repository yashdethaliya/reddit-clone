package org.dev.resource;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.bson.Document;
import org.dev.Client.RedditClient;
import org.dev.Entity.*;
import org.dev.Kafka.KafkaPostProducer;
import org.dev.OpenSeacrh.OpenSearchsearchservice;
import org.dev.mongo.MongoDBService;
import org.dev.servicelayer.RedditService;
import org.dev.tokengenerator.Tokengenerator;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Path("/")
public class RedditResource {
    private static final Tracer tracer = GlobalOpenTelemetry.getTracer("reddit-service");

    @Inject
    KafkaPostProducer kafkaPostProducer;
    @Inject
            @RestClient
    RedditClient redditClient;
    @Inject
    MongoDBService mongoDBService;
    @Inject
    OpenSearchsearchservice openSearchClient;
    @Inject
    RedditService redditService;
    @Inject
    Tokengenerator tokenfetch;
    @GET
    @Path("author-posts-api/{username}")
    @Produces(MediaType.APPLICATION_JSON)
    public CompletableFuture<RedditPostresponse> getPostbyAuthorName(@PathParam("username") String username, @QueryParam("limit") int limit)
    {
        var parentSpan = tracer.spanBuilder("fetch-posts-api").startSpan();
        Context parentcontext = Context.current().with(parentSpan);
        try (Scope parentScope = parentcontext.makeCurrent()){

            Set<String> existingURLs = mongoDBService.getexistingURLs(username);
            return CompletableFuture.supplyAsync(() -> {
                try (Scope asyncScope = parentcontext.makeCurrent()) {
                    String token;
                    var tokenSpan = tracer.spanBuilder("Fetch token for API calls").startSpan();
                    try (Scope tokenScope = parentcontext.with(tokenSpan).makeCurrent()) {
                        token = "Bearer " + tokenfetch.getToken();
                    } finally {
                        tokenSpan.end();
                    }
                    String after = null;
                    RedditPostresponse allPosts = new RedditPostresponse();
                    RedditData allData = new RedditData();
                    allPosts.setKind("Listing");
                    String res = "";
                    int curnumofpost = 0;
                    do {
                        var childSpan = tracer.spanBuilder("Call Reddit fetch API to fetch the posts")
                                .setAttribute("username", username)
                                .setAttribute("limit", limit)
                                .setAttribute("after", after)
                                .startSpan();
                        try (Scope childScope = parentcontext.with(childSpan).makeCurrent()) {
                            res = redditClient.getPostsByUser(username, limit, after, token);
                        } finally {
                            childSpan.end();
                        }
                        RedditPostresponse response = redditService.parseRedditResponse(res);
                        if (response.getData() != null) {
                            List<RedditChildren> children = response.getData().getChildren();
                            List<RedditChildren> newPosts = new ArrayList<>();
                            for (RedditChildren child : children) {
                                String url = child.getData().getUrl();
                                if (!existingURLs.contains(url)) {
                                    newPosts.add(child);
                                    existingURLs.add(url);
                                }
                                if (newPosts.size() + curnumofpost >= limit) {
                                    break;
                                }
                            }
                            allData.getChildren().addAll(newPosts);
                            curnumofpost += newPosts.size();
                            if (curnumofpost > limit) break;
                        }
                        after = response.getData().getAfter();
                    } while (after != null && curnumofpost < limit);
                    if(allData.getChildren().isEmpty())
                    {
                        List<Document> postsdata=mongoDBService.getpostsfromdb(limit);
                        allData= redditService.maptoredditposts(postsdata);
                    }
                    allPosts.setData(allData);
                    kafkaPostProducer.sendPostsToKafka(allPosts,parentcontext);
                    return allPosts;
                }
            });
        }
        finally {
            parentSpan.end();
        }
    }

    @GET
    @Path("posts-by-keyword/{keyval}")
    public List<RedditResponseWrapper> getPostbyKeywords(@PathParam("keyval") String keyval,@QueryParam("value") String value,@QueryParam("limit") int limit) throws IOException {
        var parentSpan = tracer.spanBuilder("Search-posts").startSpan();
        Context parentcontext = Context.current().with(parentSpan);
        try(Scope parentScope = parentcontext.makeCurrent()) {
            String res = openSearchClient.searchbykeyword("reddit-posts-index", keyval, value, limit,parentcontext);
            if (!res.contains("Index Not Found")) {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(res);
                JsonNode hitsNode = rootNode.path("hits").path("hits");
                List<RedditResponseWrapper> redditPosts = new ArrayList<>();
                for (JsonNode hit : hitsNode) {
                    RedditResponseWrapper wrapper = new RedditResponseWrapper();

                    wrapper.set_index(hit.path("_index").asText());
                    wrapper.set_id(hit.path("_id").asText());
                    wrapper.set_score(hit.path("_score").asDouble());

                    JsonNode sourceNode = hit.path("_source");
                    RedditResponse post = new RedditResponse();

                    post.setTitle(sourceNode.path("title").asText());
                    post.setUrl(sourceNode.path("url").asText());
                    post.setAuthor(sourceNode.path("author").asText());
                    post.setScore(sourceNode.path("score").asInt());
                    post.setCreatedUtc(sourceNode.path("created_utc").asLong());
                    post.setAuthorFullname(sourceNode.path("author_fullname").asText());
                    post.setSubreddit(sourceNode.path("subreddit").asText());
                    post.setSelftext(sourceNode.path("selftext").asText());
                    post.setThumbnail(sourceNode.path("thumbnail").asText());

                    wrapper.setRedditResponse(post);

                    redditPosts.add(wrapper);
                }
                return redditPosts;
            } else {
                List<RedditResponseWrapper> blank_res = new ArrayList<>();
                return blank_res;

            }
        }
        finally {
            parentSpan.end();
        }

    }
    @GET
    @Path("trending-users")
    public List<TrendingUser> findtrendinguser()
    {
        var parentSpan = tracer.spanBuilder("Find Trending users").startSpan();
        Context parentcontext = Context.current().with(parentSpan);
        try(Scope parentscope=parentcontext.makeCurrent()) {
            return mongoDBService.finetredingusers(parentcontext);
        }
        finally {
            parentSpan.end();
        }
    }

}
