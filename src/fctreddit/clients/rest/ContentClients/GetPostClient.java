package fctreddit.clients.rest.ContentClients;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Logger;

import fctreddit.Discovery;

public class GetPostClient {
	
	private static Logger Log = Logger.getLogger(GetPostClient.class.getName());


	public static void main(String[] args) throws IOException {
		
		if( args.length != 1) {
			System.err.println( "Use: java " + GetPostClient.class.getCanonicalName() + " postId");
			return;
		}
		
		Discovery discovery = new Discovery(Discovery.DISCOVERY_ADDR);
		discovery.start();

		URI[] uris = discovery.knownUrisOf("Content", 1);

		String postId = args[0];
		
		var client = new RestContentClient( URI.create( uris[0].toString() ) );
			
		var result = client.getPost(postId);
		if( result.isOK()  )
			Log.info("Get Post :" + result.value() );
		else
			Log.info("Get Post failed with error: " + result.error());
		
	}
	
}