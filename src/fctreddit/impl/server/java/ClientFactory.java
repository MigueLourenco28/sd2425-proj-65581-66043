package fctreddit.impl.server.java;

import fctreddit.Discovery;
import fctreddit.api.java.Content;
import fctreddit.api.java.Image;
import fctreddit.api.java.Users;
import fctreddit.clients.grpc.GrpcUsersClient;
import fctreddit.clients.rest.ContentClients.RestContentClient;
import fctreddit.clients.rest.ImageClients.RestImageClient;
import fctreddit.clients.rest.UserClients.RestUsersClient;

import java.io.IOException;
import java.net.URI;

public class ClientFactory {

    private static Discovery discovery;
    private static ClientFactory instance;

    private ClientFactory() throws IOException {
        discovery = new Discovery(Discovery.DISCOVERY_ADDR);
        discovery.start();
    }

    public static ClientFactory getInstance() throws IOException {
        if(instance == null) {
            instance = new ClientFactory();
        }
        return instance;
    }

    public Users getUserClient() throws IOException {
        URI[] uris = discovery.knownUrisOf("Users", 1);
        String[] splitUri = uris[0].toString().split("/");

        if(splitUri[2].equals("rest")){
            return new RestUsersClient(uris[0]);
        }else if (splitUri[2].equals("grpc")){
            return null;
        }else{
            throw new IOException("Not supported yet.");
        }
    }

    public Image getImageClient() throws IOException {
        URI[] uris = discovery.knownUrisOf("Image", 1);
        String[] splitUri = uris[0].toString().split("/");

        if(splitUri[2].equals("rest")){
            return new RestImageClient(uris[0]);
        }else if (splitUri[2].equals("grpc")){
            return null;
        }else{
            throw new IOException("Not supported yet.");
        }
    }

    public Content getContentClient() throws IOException {
        URI[] uris = discovery.knownUrisOf("Content", 1);
        String[] splitUri = uris[0].toString().split("/");

        if(splitUri[2].equals("rest")){
            return new RestContentClient(uris[0]);
        }else if (splitUri[2].equals("grpc")){
            return null;
        }else{
            throw new IOException("Not supported yet.");
        }
    }


}
