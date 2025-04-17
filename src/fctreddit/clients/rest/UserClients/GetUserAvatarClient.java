package fctreddit.clients.rest.UserClients;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.glassfish.jersey.client.ClientConfig;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import fctreddit.Discovery;
import fctreddit.api.rest.RestUsers;

public class GetUserAvatarClient {


	public static void main(String[] args) throws IOException {
		
		if( args.length != 2) {
			System.err.println( "Use: java " + CreateUserClient.class.getCanonicalName() + " url userId filenameToSaveData");
			return;
		}

		Discovery discovery = new Discovery(Discovery.DISCOVERY_ADDR);
		discovery.start();

		URI[] uris = discovery.knownUrisOf("Users", 1);

		String userId = args[0];
		String filenameToSave = args[1];
		
		System.out.println("Sending request to server.");
		
		ClientConfig config = new ClientConfig();
		Client client = ClientBuilder.newClient(config);
		
		WebTarget target = client.target( uris[0].toString() ).path( RestUsers.PATH );
		
		Response r = target.path( userId ).path( RestUsers.AVATAR ).request()
				.accept(MediaType.APPLICATION_OCTET_STREAM)
				.get();

		if( r.getStatus() == Status.OK.getStatusCode() && r.hasEntity() ) {
			System.out.println("Success:");
			byte[] data = r.readEntity(byte[].class);
			System.out.println( "Received user avatar with " + data.length + " bytes");
			Files.write(Paths.get(filenameToSave), data);
		} else
			System.out.println("Error, HTTP error status: " + r.getStatus() );

	}
	
}
