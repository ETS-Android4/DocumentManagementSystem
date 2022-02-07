package org.unibl.etf.util;

import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class Configuration {
	
	public static String privateKeyPath;
	public static String urlStartDocs;
	public static String urlStartUsers;
	public static String urlStartAuth;

	static {
		ResourceBundle bundle=PropertyResourceBundle.getBundle("org.unibl.etf.util.Configuration");
		privateKeyPath=bundle.getString("privateKeyPath");
		urlStartDocs=bundle.getString("urlStartDocs");
		urlStartUsers=bundle.getString("urlStartUsers");
		urlStartAuth=bundle.getString("urlStartAuth");
	}
	

}
