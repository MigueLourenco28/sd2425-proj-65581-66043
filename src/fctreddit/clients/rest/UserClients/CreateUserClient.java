package fctreddit.clients.rest.UserClients;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Logger;

import fctreddit.api.java.Result;
import fctreddit.Discovery;
import fctreddit.api.User;

public class CreateUserClient {

	private static Logger Log = Logger.getLogger(CreateUserClient.class.getName());
	
	public static void main(String[] args) throws IOException {
		
		if( args.length != 4) {
			System.err.println( "Use: java " + CreateUserClient.class.getCanonicalName() + " url userId fullName email password");
			return;
		}
		
		Discovery discovery = new Discovery(Discovery.DISCOVERY_ADDR);
		discovery.start();

		URI[] uris = discovery.knownUrisOf("Users", 1);

		String userId = args[0];
		String fullName = args[1];
		String email = args[2];
		String password = args[3];
		
		User usr = new User( userId, fullName, email, password);
		
		RestUsersClient client = new RestUsersClient( URI.create( uris[0].toString() ) );
		
		Result<String> result = client.createUser( usr );
		if( result.isOK()  )
			Log.info("Created user:" + result.value() );
		else
			Log.info("Create user failed with error: " + result.error());

	}
	
}
