package org.unibl.etf.model.dto;

import java.io.Serializable;
import java.sql.Timestamp;

public class User implements Serializable{

	private int userId;
	private String username;
	private String password;
	private String token;
	private Timestamp tokenExpiration;
	private byte[] salt;

	public User() {
		// TODO Auto-generated constructor stub
	}

	public User(int userId, String username, String password, String token, Timestamp tokenExpiration, byte[] salt) {
		super();
		this.userId = userId;
		this.username = username;
		this.password = password;
		this.token = token;
		this.tokenExpiration = tokenExpiration;
		this.salt = salt;
	}



	public int getUserId() {
		return userId;
	}


	public void setUserId(int userId) {
		this.userId = userId;
	}


	public String getUsername() {
		return username;
	}


	public void setUsername(String username) {
		this.username = username;
	}


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}


	public String getToken() {
		return token;
	}


	public void setToken(String token) {
		this.token = token;
	}



	public Timestamp getTokenExpiration() {
		return tokenExpiration;
	}


	public void setTokenExpiration(Timestamp tokenExpiration) {
		this.tokenExpiration = tokenExpiration;
	}

	public byte[] getSalt() {
		return salt;
	}

	public void setSalt(byte[] salt) {
		this.salt = salt;
	}
	
	

}