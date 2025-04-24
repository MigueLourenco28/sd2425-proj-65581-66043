package fctreddit.clients.rest.ContentClients;

import fctreddit.api.Post;
import fctreddit.api.java.Result;
import fctreddit.api.rest.RestContent;
import fctreddit.api.rest.RestImage;
import fctreddit.clients.java.ContentClient;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;

import java.net.URI;
import java.util.List;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;

public class RestContentClient extends ContentClient {

    protected static final int READ_TIMEOUT = 5000;
    protected static final int CONNECT_TIMEOUT = 5000;

    protected static final int MAX_RETRIES = 10;
    protected static final int RETRY_SLEEP = 5000;


    final URI serverURI;
    final Client client;
    final ClientConfig config;

    final WebTarget target;

    public RestContentClient(URI serverURI) {
        this.serverURI = serverURI;

        this.config = new ClientConfig();

        config.property(ClientProperties.READ_TIMEOUT, READ_TIMEOUT);
        config.property(ClientProperties.CONNECT_TIMEOUT, CONNECT_TIMEOUT);


        this.client = ClientBuilder.newClient(config);

        target = client.target(serverURI).path(RestContent.PATH);
    }

    @Override
    public Result<String> createPost(Post post, String userPassword) {
        return null;
    }

    @Override
    public Result<List<String>> getPosts(long timestamp, String sortOrder) {
        return null;
    }

    @Override
    public Result<Post> getPost(String postId) {
        return null;
    }

    @Override
    public Result<List<String>> getPostAnswers(String postId, long maxTimeout) {
        return null;
    }

    @Override
    public Result<Post> updatePost(String postId, String userPassword, Post post) {
        return null;
    }

    @Override
    public Result<Void> deletePost(String postId, String userPassword) {
        return null;
    }

    @Override
    public Result<Void> upVotePost(String postId, String userId, String userPassword) {
        return null;
    }

    @Override
    public Result<Void> removeUpVotePost(String postId, String userId, String userPassword) {
        return null;
    }

    @Override
    public Result<Void> downVotePost(String postId, String userId, String userPassword) {
        return null;
    }

    @Override
    public Result<Void> removeDownVotePost(String postId, String userId, String userPassword) {
        return null;
    }

    @Override
    public Result<Integer> getupVotes(String postId) {
        return null;
    }

    @Override
    public Result<Integer> getDownVotes(String postId) {
        return null;
    }


    @Override
    public Result<Void> deletedUser(String userId, String password) {
        Response r = executeOperationsDelete(target.path(userId).queryParam(RestContent.PASSWORD, password)
                .request().accept(MediaType.APPLICATION_JSON));
        if (r == null) {
            return Result.error(Result.ErrorCode.TIMEOUT);
        }

        int status = r.getStatus();
        if (status != Response.Status.OK.getStatusCode()) {
            return Result.error(getErrorCodeFrom(status));
        } else {
            return Result.ok(r.readEntity(void.class));
        }
    }


    public static Result.ErrorCode getErrorCodeFrom(int status) {
        return switch (status) {
            case 200, 209 -> Result.ErrorCode.OK;
            case 409 -> Result.ErrorCode.CONFLICT;
            case 403 -> Result.ErrorCode.FORBIDDEN;
            case 404 -> Result.ErrorCode.NOT_FOUND;
            case 400 -> Result.ErrorCode.BAD_REQUEST;
            case 500 -> Result.ErrorCode.INTERNAL_ERROR;
            case 501 -> Result.ErrorCode.NOT_IMPLEMENTED;
            default -> Result.ErrorCode.INTERNAL_ERROR;
        };
    }
}
