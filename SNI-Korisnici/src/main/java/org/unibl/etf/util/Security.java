package org.unibl.etf.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import org.json.JSONObject;
import org.unibl.etf.model.dto.User;

public class Security {

	public static final String CSP_POLICY="default-src 'self'; font-src https://fonts.gstatic.com; script-src 'self' https://code.getmdl.io https://fonts.googleapis.com 'unsafe-inline'; img-src * data: ; style-src 'self' https://fonts.googleapis.com https://code.getmdl.io ; frame-src 'none'; base-uri 'none';";
	
	private static final String IP_ADDRESS="^\\b((25[0-5]|2[0-4]\\d|[01]\\d\\d|\\d?\\d)\\.){3}(25[0-5]|2[0-4]\\d|[01]\\d\\d|\\d?\\d)\\b$";
	private static final String NAME="^[a-zA-Z0-9 .-]+$";
	private static final String ROLE="^A$|^AD$|^K$";
	private static final String FILENAME="^(?!^(PRN|AUX|CLOCK\\$|NUL|CON|COM\\d|LPT\\d|\\..*)(\\..+)?$)[^\\x00-\\x1f\\\\?*:\\\";|/<>]+$";
	private static final String FILEPATH="^(?!^(PRN|AUX|CLOCK\\$|NUL|CON|COM\\d|LPT\\d|\\..*)(\\..+)?$)[^\\x00-\\x1f\\\\?*:\\\";|<>]+$";
	
	private static final String PERMISSIONS="^c{0,1}r{0,1}u{0,1}d{0,1}$";
	private static final String MAIL="^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
	
	public static boolean filterIP(String ipAddress) {
		return ipAddress.matches(IP_ADDRESS);
	}
	
	public static boolean filterUsername(String username) {
		return username.matches(NAME);
	}
	
	public static boolean filterRole(String role) {
		return role.matches(ROLE);
	}
	
	public static boolean filterPermissions(String permissions) {
		return permissions.matches(PERMISSIONS);
	}
	
	public static boolean filterFilename(String filename) {
		if(filename.contains("..")) {
			return false;
		}
		return filename.matches(FILENAME);
	}
	
	public static boolean filterMail(String mail) {
		return mail.matches(MAIL);
	}
	
	
	public static boolean filterFilePath(String path) {
		if(path.contains("..")) {
			return false;
		}
		return path.matches(FILEPATH);
		/*String parts[]=path.split("/");
		for(String p:parts) {
			if(!p.matches(FILENAME)) {
				return false;
			}
		}
		return true;*/
	}
	
	public static boolean checkJsonForUser(JSONObject json) {
		return json.has("username") && json.has("password") && json.has("role") && json.has("rootDir") && json.has("mail")
				&& json.has("ipAddress") && json.has("create") && json.has("update") && json.has("read") && json.has("delete");
	}
	
	public static boolean isValidInput(User u) {
		return filterUsername(u.getUsername()) && filterRole(u.getRole()) && filterFilePath(u.getRootDir()) && (!u.getMail().isEmpty()?filterMail(u.getMail()):true) &&
				filterPermissions(u.getPermissions()) && (u.getIpAddress()!=null?filterIP(u.getIpAddress()):true);
	}
	
	public static PublicKey getPublicKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		byte[] keyBytes=Files.readAllBytes(Paths.get(Configuration.publicKeyPath));
		
		X509EncodedKeySpec spec=new X509EncodedKeySpec(keyBytes);
		KeyFactory kf=KeyFactory.getInstance("RSA");
		return kf.generatePublic(spec);
	}

}
