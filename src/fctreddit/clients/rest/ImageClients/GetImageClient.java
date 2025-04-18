package fctreddit.clients.rest.ImageClients;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Logger;

import fctreddit.api.java.Result;
import fctreddit.Discovery; 

public class GetImageClient {

	private static Logger Log = Logger.getLogger(GetImageClient.class.getName());
	
	public static void main(String[] args) throws IOException {
		
		if( args.length != 2) {
			System.err.println( "Use: java " + GetImageClient.class.getCanonicalName() + "userId imageId");
			return;
		}
		
		Discovery discovery = new Discovery(Discovery.DISCOVERY_ADDR);
		discovery.start();

		URI[] uris = discovery.knownUrisOf("Image", 1);

		String userId = args[0];
		String imageId = args[1];
		
		RestImageClient client = new RestImageClient( URI.create( uris[0].toString() ) );
		
		Result<byte[]> result = client.getImage( userId, imageId );
		if( result.isOK()  )
			Log.info("Get image:" + result.value() );
		else
			Log.info("Get image failed with error: " + result.error());

	}
	
}
