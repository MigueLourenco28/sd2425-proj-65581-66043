package fctreddit.clients.rest.ContentClients;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Logger;

import fctreddit.api.java.Result;
import fctreddit.Discovery;
import fctreddit.api.Post;

public class CreatePostClient {

	private static Logger Log = Logger.getLogger(CreatePostClient.class.getName());
	
	public static void main(String[] args) throws IOException {
		
		if( args.length != 3) {
			System.err.println( "Use: java " + CreatePostClient.class.getCanonicalName() + " authorId content userPassword");
			return;
		}
		
		Discovery discovery = new Discovery(Discovery.DISCOVERY_ADDR);
		discovery.start();

		URI[] uris = discovery.knownUrisOf("Content", 1);

		String authorId = args[0];
		String content = args[1];
        String userPassword = args[2];
		
		Post post = new Post( authorId, content );
		
		RestContentClient client = new RestContentClient( URI.create( uris[0].toString() ) );
		
		Result<String> result = client.createPost( post, userPassword );
		if( result.isOK()  )
			Log.info("Created post:" + result.value() );
		else
			Log.info("Create post failed with error: " + result.error());

	}
	
}