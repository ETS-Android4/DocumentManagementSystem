package org.unibl.etf.model.beans;

import java.sql.Timestamp;
import java.util.Calendar;

import org.unibl.etf.model.dao.UserDAO;
import org.unibl.etf.model.dto.User;

public class UserBean {
	
	private User user;
	private boolean loggedIn=false;

	public UserBean() {
		// TODO Auto-generated constructor stub
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public boolean isLoggedIn() {
		return loggedIn;
	}

	public void setLoggedIn(boolean loggedIn) {
		this.loggedIn = loggedIn;
	}
	
	public boolean checkToken(String token) {
		User u=UserDAO.getByUsernameAndPasswordAndToken(user, token);
		if(u!=null) {
			//provjeri expiration, user postoji sa kredencijalima
			Calendar calendar = Calendar.getInstance();
			Timestamp currTimestamp = new Timestamp(calendar.getTimeInMillis());
			if(currTimestamp.before(u.getTokenExpiration())) {
				user=u;
				loggedIn=true;
				
				//update token expirationa
				u.setTokenExpiration(currTimestamp);
				UserDAO.updateTokenExp(u);  
				
				return true;
			}else {
				return false;
			}
		}
		return false;
	}

}
