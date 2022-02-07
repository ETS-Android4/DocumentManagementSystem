package org.unibl.etf.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

public class Security {
	
//	public static final String CSP_POLICY="script-src 'self' https://code.getmdl.io https://apis.google.com;";
	
	private static final String FILENAME="^(?!^(PRN|AUX|CLOCK\\$|NUL|CON|COM\\d|LPT\\d|\\..*)(\\..+)?$)[^\\x00-\\x1f\\\\?*:\\\";|/<>]+$";
	private static final String FILEPATH="^(?!^(PRN|AUX|CLOCK\\$|NUL|CON|COM\\d|LPT\\d|\\..*)(\\/|\\..+)?$)[^\\x00-\\x1f\\\\?*:\\\";|<>]+$";
	
	public static boolean filterFilename(String filename) {
		if(filename.contains("..")) {
			return false;
		}
		return filename.matches(FILENAME);
	}
	
	public static boolean filterFilepath(String filepath) {
		return filepath.matches(FILEPATH);
	}
	
	public static boolean filterFilePath(String path) {
		String parts[]=path.split("/");
		for(String p:parts) {
			if(!p.matches(FILENAME)) {
				return false;
			}
		}
		return true;
	}
	
	public static PublicKey getPublicKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		byte[] keyBytes=Files.readAllBytes(Paths.get(Configuration.publicKeyPath));
		
		X509EncodedKeySpec spec=new X509EncodedKeySpec(keyBytes);
		KeyFactory kf=KeyFactory.getInstance("RSA");
		return kf.generatePublic(spec);
	}
	
}
