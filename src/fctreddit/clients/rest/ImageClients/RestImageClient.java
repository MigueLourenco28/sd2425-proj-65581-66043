package fctreddit.clients.rest.ImageClients;

import fctreddit.api.java.Result;
import fctreddit.api.rest.RestUsers;
import fctreddit.clients.java.ImageClient;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;

import java.net.URI;

public class RestImageClient extends ImageClient {

    protected static final int READ_TIMEOUT = 5000;
    protected static final int CONNECT_TIMEOUT = 5000;


    final URI serverURI;
    final Client client;
    final ClientConfig config;

    final WebTarget target;

    public RestImageClient(URI serverURI) {
        this.serverURI = serverURI;

        this.config = new ClientConfig();

        config.property( ClientProperties.READ_TIMEOUT, READ_TIMEOUT);
        config.property( ClientProperties.CONNECT_TIMEOUT, CONNECT_TIMEOUT);


        this.client = ClientBuilder.newClient(config);

        target = client.target( serverURI ).path( RestUsers.PATH );
    }


    @Override
    public Result<String> createImage(String userId, byte[] imageContents, String password) {
        Response r = executeOperationsPost(target.path( userId ).queryParam(RestUsers.PASSWORD, password).request()
                .accept(MediaType.APPLICATION_JSON), Entity.entity(imageContents, MediaType.APPLICATION_OCTET_STREAM));

        if(r == null){
            return Result.error(Result.ErrorCode.TIMEOUT);
        }

        int status = r.getStatus();
        if(status != Response.Status.OK.getStatusCode()){
            return Result.error(getErrorCodeFrom(status));
        }else{
            return Result.ok(r.readEntity(String.class));
        }
    }

    @Override
    public Result<byte[]> getImage(String userId, String imageId) {
        Response r = executeOperationsGet(target.path( userId ).queryParam(RestUsers.AVATAR,imageId).request()
                .accept(MediaType.APPLICATION_OCTET_STREAM));

        if(r == null){
            return Result.error(Result.ErrorCode.TIMEOUT);
        }

        int status = r.getStatus();
        if(status != Response.Status.OK.getStatusCode()){
            return Result.error(getErrorCodeFrom(status));
        }else{
            return Result.ok(r.readEntity(byte[].class));
        }
    }

    @Override
    public Result<Void> deleteImage(String userId, String imageId, String password) {
        Response r = executeOperationsDelete(target.path( userId ).queryParam(RestUsers.PASSWORD, password).queryParam(RestUsers.AVATAR,imageId)
                .request().accept(MediaType.APPLICATION_JSON));


        if(r == null){
            return Result.error(Result.ErrorCode.TIMEOUT);
        }

        int status = r.getStatus();
        if(status != Response.Status.OK.getStatusCode()){
            return Result.error(getErrorCodeFrom(status));
        }else{
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
