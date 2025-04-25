package fctreddit.impl.server.java;

import fctreddit.Discovery;
import fctreddit.api.java.Content;
import fctreddit.api.java.Image;
import fctreddit.api.java.Users;
import fctreddit.clients.grpc.GrpcImageClient;
import fctreddit.clients.grpc.GrpcUsersClient;
import fctreddit.clients.grpc.GrpcContentClient;
import fctreddit.clients.rest.ContentClients.RestContentClient;
import fctreddit.clients.rest.ImageClients.RestImageClient;
import fctreddit.clients.rest.UserClients.RestUsersClient;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Logger;

public class ClientFactory {

    private static Discovery discovery;
    private static ClientFactory instance;


    //private static Logger Log = Logger.getLogger(ClientFactory.class.getName());

    private ClientFactory() throws IOException {
        discovery = new Discovery(Discovery.DISCOVERY_ADDR);
        discovery.start();
    }

    public static ClientFactory getInstance() throws IOException {
        if (instance == null) {
            instance = new ClientFactory();
        }
        return instance;
    }

    public Users getUserClient() throws IOException {
        String[] splitUri;
        URI[] uris;
        try {
            uris = discovery.knownUrisOf("Users", 1);
            splitUri = uris[0].toString().split("/");
        } catch (Exception e) {
            //Log.info(e.getMessage());
            throw new IOException(e);
        }


        if (splitUri[3].equals("rest")) {
            return new RestUsersClient(uris[0]);
        } else if (splitUri[3].equals("grpc")) {
            return new GrpcUsersClient(uris[0]);
        } else {
            throw new IOException("Not supported yet.");
        }
    }

    public Image getImageClient() throws IOException {
        String[] splitUri;
        URI[] uris;
        try {
            uris = discovery.knownUrisOf("Image", 1);
            splitUri = uris[0].toString().split("/");
        } catch (Exception e) {
            //Log.info(e.getMessage());
            throw new IOException(e);
        }

        if (splitUri[3].equals("rest")) {
            return new RestImageClient(uris[0]);
        } else if (splitUri[3].equals("grpc")) {
            return new GrpcImageClient(uris[0]);
        } else {
            throw new IOException("Not supported yet.");
        }
    }

    public Content getContentClient() throws IOException {
        URI[] uris = discovery.knownUrisOf("Content", 1);
        String[] splitUri = uris[0].toString().split("/");

        if (splitUri[3].equals("rest")) {
            return new RestContentClient(uris[0]);
        } else if (splitUri[3].equals("grpc")) {
            return new GrpcContentClient(uris[0]);
        } else {
            throw new IOException("Not supported yet.");
        }
    }

    public URI getURIClient(String service) throws IOException {
        URI[] uris = discovery.knownUrisOf(service, 1);
        return uris[0];
    }


}
