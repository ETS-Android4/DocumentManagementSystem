package org.unibl.etf.util;

import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class Configuration {
	
	public static String rootDir;
	public static String publicKeyPath;
	public static String urlStartDocs;
	public static String urlStartUsers;
	public static String urlStartAuth;

	static {
		ResourceBundle bundle=PropertyResourceBundle.getBundle("org.unibl.etf.util.Configuration");
		rootDir=bundle.getString("rootDir");
		publicKeyPath=bundle.getString("publicKeyPath");
		urlStartDocs=bundle.getString("urlStartDocs");
		urlStartUsers=bundle.getString("urlStartUsers");
		urlStartAuth=bundle.getString("urlStartAuth");
	}
	

}
