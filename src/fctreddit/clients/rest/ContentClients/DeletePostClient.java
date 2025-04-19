package fctreddit.clients.rest.UserClients;

import java.io.IOException;
import java.net.URI;

import org.glassfish.jersey.client.ClientConfig;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import fctreddit.Discovery;
import fctreddit.api.rest.RestContent;
import fctreddit.api.rest.RestUsers;

public class DeletePostClient {

	public static void main(String[] args) throws IOException {
		
		if( args.length != 2) {
			System.err.println( "Use: java " + DeletePostClient.class.getCanonicalName() + " postId userPassword");
			return;
		}
		
		Discovery discovery = new Discovery(Discovery.DISCOVERY_ADDR);
		discovery.start();

		URI[] uris = discovery.knownUrisOf("Content", 1);

		String postId = args[0];
		String userPassword = args[1];
		
		System.out.println("Sending request to server.");
		
		ClientConfig config = new ClientConfig();
        Client client = ClientBuilder.newClient(config);

        WebTarget target = client.target( uris[0].toString() ).path(RestContent.PATH).path(postId)
                .queryParam(RestContent.PASSWORD, userPassword);

        Response r = target.request().delete();

        if (r.getStatus() == Status.OK.getStatusCode()) {
            System.out.println("Post successfully deleted.");
        } else {
            System.out.println("Error, HTTP error status: " + r.getStatus());
        }
	}
	
}
