package com.bricklink.api.example;

import java.util.Collections;
import java.util.Map;

import org.json.simple.JSONObject;

public class BLAuthTest {
	
	@SuppressWarnings("unchecked")
	public static void main( String[] args ) {
		String consumerKey = "your consumer key";
		String consumerSecret = "your consumer secret";
		String tokenValue = "your access token";
		String tokenSecret = "your token secret";

		BLAuthSigner signer = new BLAuthSigner( consumerKey, consumerSecret );
		signer.setToken( tokenValue, tokenSecret );
		signer.setVerb( "GET" );
		signer.setURL( "https://api.bricklink.com/api/store/v1/orders" );
		signer.addParameter( "direction", "in" );
		
		Map<String, String> params = Collections.emptyMap();
		
		try {
			params = signer.getFinalOAuthParams();
		} catch( Exception e ) {
			e.printStackTrace();
		}
		
		JSONObject obj = new JSONObject();
		obj.putAll( params );
		
		System.out.println( obj.toJSONString() );
	}
}
