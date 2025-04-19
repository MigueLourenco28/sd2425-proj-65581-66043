package fctreddit.clients.rest.ImageClients;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Logger;

import fctreddit.api.java.Result;
import fctreddit.Discovery; 

public class DeleteImageClient {

	private static Logger Log = Logger.getLogger(DeleteImageClient.class.getName());
	
	public static void main(String[] args) throws IOException {
		
		if( args.length != 3) {
			System.err.println( "Use: java " + DeleteImageClient.class.getCanonicalName() + "userId imageId password");
			return;
		}
		
		Discovery discovery = new Discovery(Discovery.DISCOVERY_ADDR);
		discovery.start();

		URI[] uris = discovery.knownUrisOf("Image", 1);

		String userId = args[0];
		String imageId = args[1];
        String password = args[2];
		
		RestImageClient client = new RestImageClient( URI.create( uris[0].toString() ) );
		
		Result<Void> result = client.deleteImage( userId, imageId, password );
		if( result.isOK()  )
			Log.info("Deleted image:" + imageId );
		else
			Log.info("Delete image failed with error: " + result.error());

	}
	
}
