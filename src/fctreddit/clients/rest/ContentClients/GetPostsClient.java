package fctreddit.clients.rest.ContentClients;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Logger;

import fctreddit.Discovery;

public class GetPostsClient {
	
	private static Logger Log = Logger.getLogger(GetPostsClient.class.getName());


	public static void main(String[] args) throws IOException {
		
		if( args.length != 2) {
			System.err.println( "Use: java " + GetPostsClient.class.getCanonicalName() + " timestamp sortOrder");
			return;
		}
		
		Discovery discovery = new Discovery(Discovery.DISCOVERY_ADDR);
		discovery.start();

		URI[] uris = discovery.knownUrisOf("Content", 1);

		String sortOrder = args[0];
        String timestamp = args[1];
		
		var client = new RestContentClient( URI.create( uris[0].toString() ) );
			
		var result = client.getPosts( Long.parseLong(timestamp) , sortOrder);
		if( result.isOK()  )
			Log.info("Get Post :" + result.value() );
		else
			Log.info("Get Post failed with error: " + result.error());
		
	}
	
}