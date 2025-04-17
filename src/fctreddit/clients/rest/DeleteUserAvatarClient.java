package fctreddit.clients.rest;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.glassfish.jersey.client.ClientConfig;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import fctreddit.Discovery;
import fctreddit.api.rest.RestUsers;

public class DeleteUserAvatarClient {

	public static void main(String[] args) throws IOException {
		
		if( args.length != 2) {
			System.err.println( "Use: java " + CreateUserClient.class.getCanonicalName() + " url userId password");
			return;
		}

		Discovery discovery = new Discovery(Discovery.DISCOVERY_ADDR);
		discovery.start();

		URI[] uris = discovery.knownUrisOf("Users", 1);
		
		String userId = args[0];
		String password = args[1];

		System.out.println("Sending request to server.");
		
		//----------------Added code------------------//
		ClientConfig config = new ClientConfig();
        Client client = ClientBuilder.newClient(config);

        WebTarget target = client.target( uris[0].toString() ).path( RestUsers.PATH );


        Response r = target.path( userId ).path( RestUsers.AVATAR )
                .queryParam(RestUsers.PASSWORD, password).request()
                .delete();


        if( r.getStatus() == Status.NO_CONTENT.getStatusCode() )
            System.out.println("User Avatar deleted.");
        else
            System.out.println("Error, HTTP error status: " + r.getStatus() );
		//---------------------------------------------//
	}
	
}
