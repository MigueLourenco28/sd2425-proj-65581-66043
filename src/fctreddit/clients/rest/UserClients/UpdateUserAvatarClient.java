package fctreddit.clients.rest.UserClients;

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

public class UpdateUserAvatarClient {

	public static void main(String[] args) throws IOException {
		
		if( args.length != 3) {
			System.err.println( "Use: java " + CreateUserClient.class.getCanonicalName() + " url userId password filename");
			return;
		}
		
		Discovery discovery = new Discovery(Discovery.DISCOVERY_ADDR);
		discovery.start();

		URI[] uris = discovery.knownUrisOf("Users", 1);

		String userId = args[0];
		String password = args[1];
		String filename = args[2];
		
		Path avatarFilePath = Paths.get(filename);
		
		if(!Files.exists(avatarFilePath)) {
			System.err.println("File " + filename + " does not exists.");
			System.exit(1);
		}
		
		byte[] avatarData = Files.readAllBytes(avatarFilePath);
		
		if(avatarData.length == 0) {
			System.err.println("File " + filename + " has zero bytes (empty file).");
			System.exit(1);
		}
			
		System.out.println("Sending request to server.");
		
		ClientConfig config = new ClientConfig();
		Client client = ClientBuilder.newClient(config);
		
		WebTarget target = client.target( uris[0].toString() ).path( RestUsers.PATH );  
		
		Response r = target.path( userId ).path( RestUsers.AVATAR )
				.queryParam(RestUsers.PASSWORD, password).request()
				.put(Entity.entity(avatarData, MediaType.APPLICATION_OCTET_STREAM));

		if( r.getStatus() == Status.NO_CONTENT.getStatusCode() ) 
			System.out.println("Success.");
		 else
			System.out.println("Error, HTTP error status: " + r.getStatus() );
	}
	
}
