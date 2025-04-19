package fctreddit.clients.rest.ContentClients;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Logger;

import fctreddit.Discovery;
import fctreddit.api.Post;

public class UpdatePostClient {
	
	private static Logger Log = Logger.getLogger(UpdatePostClient.class.getName());


	public static void main(String[] args) throws IOException {
		
		if( args.length != 5) {
			System.err.println( "Use: java " + UpdatePostClient.class.getCanonicalName() + " postId userPassword authorId content parentUrl");
			return;
		}
		
		Discovery discovery = new Discovery(Discovery.DISCOVERY_ADDR);
		discovery.start();

		URI[] uris = discovery.knownUrisOf("Content", 1);

		String postId = args[0];
        String userPassword = args[1];
        String authorId = args[2];
        String content = args[3];
        String parentUrl = args[4];

        Post post = new Post(authorId, content, parentUrl);

		var client = new RestContentClient( URI.create( uris[0].toString() ) );
			
		var result = client.updatePost(postId, userPassword, post);
		if( result.isOK()  )
			Log.info("Updated Post:" + result.value() );
		else
			Log.info("Update Post failed with error: " + result.error());
		
	}
	
}