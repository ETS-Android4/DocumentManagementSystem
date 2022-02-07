package org.unibl.etf.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONObject;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;

public class IdTokenVerifier {

	private static final String GOOGLE_CLIENT_ID = "702701829361-fnbmf2buer7fidl1u6qvsb7ur6cuks9n.apps.googleusercontent.com";

	public static GoogleIdToken.Payload getPayload (String tokenString) throws Exception {
		GsonFactory gsonFactory = new GsonFactory();
		GoogleIdTokenVerifier googleIdTokenVerifier = new GoogleIdTokenVerifier(new NetHttpTransport(), gsonFactory);
		
		GoogleIdToken token = GoogleIdToken.parse(gsonFactory, tokenString);
	    if (googleIdTokenVerifier.verify(token)) {
            GoogleIdToken.Payload payload = token.getPayload();
            if (!GOOGLE_CLIENT_ID.equals(payload.getAudience())) {
            	System.out.println("Audience mismatch");
            	return null;
            } else if (!GOOGLE_CLIENT_ID.equals(payload.getAuthorizedParty())) {
            	System.out.println("Client ID mismatch");
            	return null;
            }
            return payload;
         } else {
        	 System.out.println("Token can't be verified");
           	 return null;
         }
	}
	
	public static void revokeToken(String tokenString) throws IOException {
		System.out.println("Revoke");
		
	    URL url = new URL("https://oauth2.googleapis.com/revoke");
	    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	    conn.setRequestMethod("POST");
	    conn.setDoOutput(true);
	    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
	
		// slanje body dijela
		OutputStream os = conn.getOutputStream();
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
		writer.write("token="+tokenString);
		writer.flush();
		writer.close();
		os.close();
		conn.connect();
		// prijem odgovora na zahtjev
		if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
			throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
		}else {
			System.out.println("ok revoke");
		}
		// citanje odgovora
	/*	BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
		String output;
		while ((output = br.readLine()) != null) {
			System.out.println(output);
		}*/
	//	os.close();
	//	br.close();
	//	conn.disconnect();
	    
	}
	
}
