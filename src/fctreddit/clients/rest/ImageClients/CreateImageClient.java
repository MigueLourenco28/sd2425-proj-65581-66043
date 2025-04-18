package fctreddit.clients.rest.ImageClients;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Logger;

import fctreddit.api.java.Result;
import fctreddit.Discovery; 

public class CreateImageClient {

	private static Logger Log = Logger.getLogger(CreateImageClient.class.getName());
	
	public static void main(String[] args) throws IOException {
		
		if( args.length != 3) {
			System.err.println( "Use: java " + CreateImageClient.class.getCanonicalName() + "userId imageContents password");
			return;
		}
		
		Discovery discovery = new Discovery(Discovery.DISCOVERY_ADDR);
		discovery.start();

		URI[] uris = discovery.knownUrisOf("Image", 1);

		String userId = args[0];
		String imageContents = args[1];
		String password = args[2];
		
		RestImageClient client = new RestImageClient( URI.create( uris[0].toString() ) );
		
		Result<String> result = client.createImage( userId, imageContents.getBytes(), password );
		if( result.isOK()  )
			Log.info("Created image:" + result.value() );
		else
			Log.info("Create image failed with error: " + result.error());

	}
	
}
