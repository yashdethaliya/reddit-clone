package org.dev.resource;


import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.dev.Client.RedditClient;
import org.dev.Entity.RedditPostresponse;
import org.dev.servicelayer.RedditService;
import org.dev.tokengenerator.Tokengenerator;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@Path("/author-posts-api")
public class RedditResource {

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
    public RedditPostresponse getPostbyAuthorName(@PathParam("username") String username, @QueryParam("limit") int limit)
    {
        String Token="Bearer " +tokenfetch.getToken();
        String res= redditClient.getPostsByUser(username,limit,Token);
        return redditService.parseRedditResponse(res);
    }

}
