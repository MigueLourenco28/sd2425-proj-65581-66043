package fctreddit.clients.rest.UserClients;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Logger;

import fctreddit.Discovery;

public class GetUserClient {
	
	private static Logger Log = Logger.getLogger(GetUserClient.class.getName());


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
		
		var client = new RestUsersClient( URI.create( uris[0].toString() ) );
			
		var result = client.getUser(userId, password);
		if( result.isOK()  )
			Log.info("Get user:" + result.value() );
		else
			Log.info("Get user failed with error: " + result.error());
		
	}
	
}
