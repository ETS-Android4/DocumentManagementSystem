package org.unibl.etf.util;

import java.net.PasswordAuthentication;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Properties;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.unibl.etf.model.dao.UserDAO;

public class MailSender {

	private static String username;
	private static String password;
	private static Properties properties;
	
	static {
		ResourceBundle bundle=PropertyResourceBundle.getBundle("org.unibl.etf.util.MailSender");
		username=bundle.getString("username");
		password=bundle.getString("password");
		
		
		properties = new Properties();

	    Enumeration<String> keys = bundle.getKeys();
	    while (keys.hasMoreElements()) {
	      String key = keys.nextElement();
	      properties.put(key, bundle.getString(key));
	    }
	}
	

	public static void sendMail( String text) {	
		System.out.println("Usao u slanje");
		ArrayList<String> recipients=UserDAO.getAllAdminMails();
		System.out.println(recipients);
		
		
		Session session=Session.getDefaultInstance(properties, new javax.mail.Authenticator() {
			protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
				return new javax.mail.PasswordAuthentication(username, password);
			}
		});
	
		for(String r: recipients) {
			try {
				Message message=new MimeMessage(session);
				message.setFrom(new InternetAddress(username));
				message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(r));
				message.setSubject("Notification");
				message.setText(text);
				Transport.send(message);
				
			}catch(MessagingException e) {
				System.out.println("Greska pri slanju");
				
			}
		}
	
	
	}
}
