package org.unibl.etf.model.dao;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

import org.unibl.etf.model.dto.User;
import org.unibl.etf.util.ConnectionPool;
import org.unibl.etf.util.DAOUtil;

//crud nad userima
public class UserDAO {

	private static final ConnectionPool pool=ConnectionPool.getInstance();
	private static final String GET_USER_BY_USERNAME="SELECT * FROM user WHERE username=? ";
	private static final String GET_USER_BY_MAIL="SELECT * FROM user WHERE mail=? ";
	private static final String GET_USER_BY_USERNAME_AND_PASSWORD_AND_TOKEN="SELECT * FROM user WHERE username=? AND password=?";
	private static final String UPDATE_USER="UPDATE user SET tokenExpiration=? WHERE userId=?";
	private static final String UPDATE_LOGOUT_TIME="UPDATE user SET logoutTime=? WHERE userId=?";
	private static final String GET_USER_BY_ROOT_DIR="SELECT * FROM user WHERE rootDir=?";
	private static final String GET_ALL_ADMIN_MAILS="SELECT mail FROM user WHERE role='A';";
	
	public static User getByUsername(String username) {
		if(username==null) {
			return null;
		}
		
		Connection conn=null;
		User result=null;
		Object[] values= {username};
		try {
			conn=pool.checkOut();
			PreparedStatement ps=DAOUtil.prepareStatement(conn, GET_USER_BY_USERNAME, false, values);
		
			ResultSet rs=ps.executeQuery();
			if(rs.next()) {
				result=new User(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6),rs.getString(7), rs.getTimestamp(8), rs.getString(9), rs.getBytes(10), rs.getString(11), rs.getTimestamp(12));
			}
			rs.close();
			ps.close();
		}catch(Exception e) {
			e.printStackTrace();
			return result;
		}finally {
			pool.checkIn(conn);
		}
		return result;
	}
	
	
	public static User getByMail(String mail) {
		if(mail==null) {
			return null;
		}
		
		Connection conn=null;
		User result=null;
		Object[] values= {mail};
		try {
			conn=pool.checkOut();
			PreparedStatement ps=DAOUtil.prepareStatement(conn, GET_USER_BY_MAIL, false, values);
		
			ResultSet rs=ps.executeQuery();
			if(rs.next()) {
				result=new User(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6),rs.getString(7), rs.getTimestamp(8), rs.getString(9), rs.getBytes(10), rs.getString(11), rs.getTimestamp(12));
			}
			rs.close();
			ps.close();
		}catch(Exception e) {
			e.printStackTrace();
			return result;
		}finally {
			pool.checkIn(conn);
		}
		return result;
	}
	
	
	public static User getByUsernameAndPassword(String username, String password) {
		if(username==null || password==null) {
			return null;
		}
		
		Connection conn=null;
		User result=null;
		Object[] values= {username};
		try {
			conn=pool.checkOut();
			PreparedStatement ps=DAOUtil.prepareStatement(conn, GET_USER_BY_USERNAME, false, values);
		
			ResultSet rs=ps.executeQuery();
			if(rs.next()) {
				result=new User(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6),rs.getString(7), rs.getTimestamp(8), rs.getString(9), rs.getBytes(10), rs.getString(11), rs.getTimestamp(12));
				String hashOfPass=hashPassword(password, result.getSalt());
				if(!hashOfPass.equals(result.getPassword())) {
					result=null;
				}
			}
			rs.close();
			ps.close();
		}catch(Exception e) {
			e.printStackTrace();
			return result;
		}finally {
			pool.checkIn(conn);
		}
		return result;
	}
	
	
	public static User getByUsernameAndPasswordAndToken(User u,  String token) {
		if(token==null) {
			return null;
		}
		
		Connection conn=null;
		User result=null;
		Object[] values= {u.getUsername(), u.getPassword()};
		try {
			conn=pool.checkOut();
			PreparedStatement ps=DAOUtil.prepareStatement(conn, GET_USER_BY_USERNAME_AND_PASSWORD_AND_TOKEN, false, values);
		
			ResultSet rs=ps.executeQuery();
			if(rs.next()) {
				result=new User(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6),rs.getString(7), rs.getTimestamp(8), rs.getString(9), rs.getBytes(10), rs.getString(11), rs.getTimestamp(12));
				token=hashPassword(token, result.getSalt());
				if(!token.equals(result.getToken())) {
					result=null;
				}
			}
			
			rs.close();
			ps.close();
		}catch(Exception e) {
			e.printStackTrace();
			return result;
		}finally {
			pool.checkIn(conn);
		}
		return result;
	}
	
	public static boolean updateLogoutTime(User u) {
		Connection conn = null;
		boolean result=false;	
		
		Calendar calendar = Calendar.getInstance();
		Timestamp currTimestamp = new Timestamp(calendar.getTimeInMillis());
		
		Object[] values= { currTimestamp,  u.getUserId()};
		
		try {
			conn=pool.checkOut();
			PreparedStatement ps=DAOUtil.prepareStatement(conn, UPDATE_LOGOUT_TIME, false, values);
			ps.executeUpdate();
		
			if(ps.getUpdateCount()>0) {
				result=true;
			}
			ps.close();
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}finally {
			pool.checkIn(conn);
		}
		return result;
	}
	
	
	public static boolean updateTokenExp(User u) {
		Connection conn = null;
		boolean result=false;	
		

		Object[] values= { u.getTokenExpiration(),  u.getUserId()};
		
		try {
			conn=pool.checkOut();
			PreparedStatement ps=DAOUtil.prepareStatement(conn, UPDATE_USER, false, values);
			ps.executeUpdate();
		
			if(ps.getUpdateCount()>0) {
				result=true;
			}
			ps.close();
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}finally {
			pool.checkIn(conn);
		}
		return result;
	}
	
	public static Boolean getByRootDir(String rootDir) {
		Connection conn=null;
		Boolean result=false;
		Object[] values= {rootDir};
		try {
			conn=pool.checkOut();
			PreparedStatement ps=DAOUtil.prepareStatement(conn, GET_USER_BY_ROOT_DIR, false, values);
		
			ResultSet rs=ps.executeQuery();
			if(rs.next()) {
				result=true;
			}
			rs.close();
			ps.close();
		}catch(Exception e) {
			e.printStackTrace();
			return null;
		}finally {
			pool.checkIn(conn);
		}
		return result;
	}
	
	
	public static ArrayList<String> getAllAdminMails(){
		Connection conn=null;
		ArrayList<String> result=new ArrayList<String>();
		
		Object[] values= {};
		try {
			conn=pool.checkOut();
			PreparedStatement ps=DAOUtil.prepareStatement(conn,GET_ALL_ADMIN_MAILS, false, values);
		
			ResultSet rs=ps.executeQuery();
			while(rs.next()) {
				result.add(rs.getString(1));
			}
			rs.close();
			ps.close();
		}catch(Exception e) {
			e.printStackTrace();
			return null;
		}finally {
			pool.checkIn(conn);
		}
		return result;
	}
	

	private static String hashPassword(String password, byte[] salt) {
		byte[] dataBytes = password.getBytes();
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(salt);
			md.update(dataBytes);
			byte[] digest = md.digest();
		
			return byteArrayToHex(digest);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	private static String byteArrayToHex(byte[] a) {
	   StringBuilder sb = new StringBuilder(a.length * 2);
	   for(byte b: a)
	      sb.append(String.format("%02x", b));
	   return sb.toString();
	}
	
}
