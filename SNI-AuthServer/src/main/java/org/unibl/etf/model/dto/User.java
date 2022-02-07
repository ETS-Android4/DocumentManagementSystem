package org.unibl.etf.model.dto;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

public class User implements Serializable{

	private int userId;
	private String username;
	private String password;
	private String role;
	private String permissions;
	private String ipAddress;
	private String token;
	private Timestamp tokenExpiration;
	private String rootDir;
	private byte[] salt;
	private String mail;
	private Timestamp logoutTime;
	
	
	public User() {
		// TODO Auto-generated constructor stub
	}

	
	public User(int userId, String username, String password, String role, String permissions, String ipAddress,
			String rootDir, String mail, Timestamp logoutTime) {
		super();
		this.userId = userId;
		this.username = username;
		this.password = password;
		this.role = role;
		this.permissions = permissions;
		this.ipAddress = ipAddress;
		this.rootDir = rootDir;
		this.mail=mail;
		this.logoutTime=logoutTime;
	}



	public User(int userId, String username, String password, String role, String permissions, String ipAddress,
			String token, Timestamp tokenExpiration, String rootDir, byte[] salt, String mail, Timestamp logoutTime) {
		super();
		this.userId = userId;
		this.username = username;
		this.password = password;
		this.role = role;
		this.permissions = permissions;
		this.ipAddress = ipAddress;
		this.token = token;
		this.tokenExpiration = tokenExpiration;
		this.rootDir = rootDir;
		this.salt=salt;
		this.mail=mail;
		this.logoutTime=logoutTime;
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


	public String getRole() {
		return role;
	}


	public void setRole(String role) {
		this.role = role;
	}


	public String getPermissions() {
		return permissions;
	}


	public void setPermissions(String permissions) {
		this.permissions = permissions;
	}


	public String getIpAddress() {
		return ipAddress;
	}


	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}


	public String getToken() {
		return token;
	}


	public void setToken(String token) {
		this.token = token;
	}


	public String getRootDir() {
		return rootDir;
	}


	public void setRootDir(String rootDir) {
		this.rootDir = rootDir;
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
	
	
	public String getMail() {
		return mail;
	}


	public void setMail(String mail) {
		this.mail = mail;
	}
	

	public Timestamp getLogoutTime() {
		return logoutTime;
	}


	public void setLogoutTime(Timestamp logoutTime) {
		this.logoutTime = logoutTime;
	}


	@Override
	public String toString() {
		return "User [userId=" + userId + ", username=" + username + ", password=" + password + ", role=" + role
				+ ", permissions=" + permissions + ", ipAddress=" + ipAddress + ", token=" + token
				+ ", tokenExpiration=" + tokenExpiration + ", rootDir=" + rootDir + "]";
	}
	
	
	

}
