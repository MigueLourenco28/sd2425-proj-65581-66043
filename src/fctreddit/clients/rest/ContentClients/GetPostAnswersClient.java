package fctreddit.clients.rest.ContentClients;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Logger;

import fctreddit.Discovery;

public class GetPostAnswersClient {
	
	private static Logger Log = Logger.getLogger(GetPostAnswersClient.class.getName());


	public static void main(String[] args) throws IOException {
		
		if( args.length != 2) {
			System.err.println( "Use: java " + GetPostAnswersClient.class.getCanonicalName() + " postId maxTimeout");
			return;
		}
		
		Discovery discovery = new Discovery(Discovery.DISCOVERY_ADDR);
		discovery.start();

		URI[] uris = discovery.knownUrisOf("Content", 1);

		String postId = args[0];
		int maxTimeout = Integer.parseInt(args[1]);
		
		var client = new RestContentClient( URI.create( uris[0].toString() ) );
			
		var result = client.getPostAnswers(postId, maxTimeout);
		if( result.isOK()  )
			Log.info("Get Post Answers:" + result.value() );
			//TODO: print the list 
		else
			Log.info("Get UpVotes failed with error: " + result.error());
		
	}
	
}