package fctreddit.clients.rest.ContentClients;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Logger;
import fctreddit.Discovery;
import fctreddit.api.java.Result;

public class DownVotePostClient {

    private static Logger Log = Logger.getLogger(DownVotePostClient.class.getName());

	public static void main(String[] args) throws IOException {
        
		if( args.length != 3) {
			System.err.println( "Use: java " + DownVotePostClient.class.getCanonicalName() + " postId userId userPassword");
			return;
		}
		
		Discovery discovery = new Discovery(Discovery.DISCOVERY_ADDR);
		discovery.start();

		URI[] uris = discovery.knownUrisOf("Content", 1);

		String postId = args[0];
		String userId = args[1];
		String userPassword = args[2];
		
		RestContentClient client = new RestContentClient( URI.create( uris[0].toString() ) );
		
		Result<Void> result = client.downVotePost( postId, userId, userPassword );
		if( result.isOK()  )
			Log.info("DownVoted post:" + result.value() );
		else
			Log.info("DownVote post failed with error: " + result.error());
	}
}
