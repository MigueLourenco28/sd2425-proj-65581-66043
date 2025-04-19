package fctreddit.clients.rest.ContentClients;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Logger;

import fctreddit.Discovery;

public class RemoveDownVotePostClient {
	
	private static Logger Log = Logger.getLogger(RemoveDownVotePostClient.class.getName());


	public static void main(String[] args) throws IOException {
		
		if( args.length != 3) {
			System.err.println( "Use: java " + RemoveDownVotePostClient.class.getCanonicalName() + " postId userId userPassword");
			return;
		}
		
		Discovery discovery = new Discovery(Discovery.DISCOVERY_ADDR);
		discovery.start();

		URI[] uris = discovery.knownUrisOf("Content", 1);

		String postId = args[0];
        String userId = args[1];
        String userPassword = args[2];
		
		var client = new RestContentClient( URI.create( uris[0].toString() ) );
			
		var result = client.removeDownVotePost(postId, userId, userPassword);
		if( result.isOK()  )
			Log.info("Removed DownVote:" + result.value() );
		else
			Log.info("Remove DownVote failed with error: " + result.error());
		
	}
	
}