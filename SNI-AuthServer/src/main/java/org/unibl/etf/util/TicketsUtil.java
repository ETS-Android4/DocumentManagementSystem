package org.unibl.etf.util;

import java.util.ArrayList;
import java.util.HashMap;

import org.unibl.etf.model.dto.User;

public class TicketsUtil {
	
	public static Object locker=new Object();
	public static final Object mapLocker=new Object();
	public static HashMap<String, User> tickets=new HashMap<String, User>();
	public static final Object namesLocker=new Object();
	public static ArrayList<String> usernames=new ArrayList<>();
	
	public static void addUsername(String name) {
		synchronized (namesLocker) {
			usernames.add(name);
		}
	}
	
	public static boolean removeUsername(String name) {
		synchronized(namesLocker) {
			return usernames.remove(name);
		}

	}
	
	public static boolean doesContain(String name) {
		synchronized(namesLocker) {
			return usernames.contains(name);
		}
	}
	
	
	public static String getTicket() {
		String ticket="";
		synchronized(locker) {
			ticket=String.valueOf(System.currentTimeMillis());
		}
		return ticket;
	}

}
