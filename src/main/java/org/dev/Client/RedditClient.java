package org.dev.Client;


import jakarta.ws.rs.*;
import org.dev.Entity.RedditPostresponse;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.security.Key;

@RegisterRestClient(baseUri = "https://oauth.reddit.com/")
@Path("user")
public interface RedditClient {
    @GET
    @Path("/{username}/submitted")
    public String getPostsByUser(@PathParam("username") String username,
                                             @QueryParam("limit") int limit,

                                             @HeaderParam("Authorization") String authHeader);
}
