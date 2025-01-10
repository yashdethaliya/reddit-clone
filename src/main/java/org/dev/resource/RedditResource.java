package org.dev.resource;


import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.dev.Client.RedditClient;
import org.dev.Entity.RedditPostresponse;
import org.dev.Entity.RedditResponse;
import org.dev.Kafka.KafkaPostProducer;
import org.dev.servicelayer.RedditService;
import org.dev.tokengenerator.Tokengenerator;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Path("/author-posts-api")
public class RedditResource {

    @Inject
    KafkaPostProducer kafkaPostProducer;
    @Inject
            @RestClient
    RedditClient redditClient;
    @Inject
    RedditService redditService;
    @Inject
    Tokengenerator tokenfetch;
    @GET
    @Path("/{username}")
    @Produces(MediaType.APPLICATION_JSON)
    public CompletableFuture<RedditPostresponse> getPostbyAuthorName(@PathParam("username") String username, @QueryParam("limit") int limit)
    {
        return CompletableFuture.supplyAsync(() -> {
            String token = "Bearer " + tokenfetch.getToken();
            String res = redditClient.getPostsByUser(username, limit, token);
            RedditPostresponse response = redditService.parseRedditResponse(res);
            kafkaPostProducer.sendPostsToKafka(response);

            return response;
        });
    }

}
