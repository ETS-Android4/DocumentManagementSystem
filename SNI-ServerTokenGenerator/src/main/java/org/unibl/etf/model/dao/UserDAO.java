package org.unibl.etf.model.dao;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import org.unibl.etf.model.dto.User;
import org.unibl.etf.util.ConnectionPool;
import org.unibl.etf.util.DAOUtil;

public class UserDAO {

	private static final ConnectionPool pool=ConnectionPool.getInstance();
	private static final String UPDATE_TOKEN="UPDATE user SET token=?, tokenExpiration=? WHERE userId=?";
	private static final String GET_USER="SELECT * FROM user u WHERE u.username=?";
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public static String updateToken(User u) {
		Connection conn = null;
		
		String tokenVal=generateToken(u.getUsername());
		String tokenHash=hash(tokenVal, u.getSalt());
		
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MINUTE, 5);
		Timestamp timestamp = new Timestamp(calendar.getTimeInMillis());
		u.setTokenExpiration(timestamp);
		
		Object[] values= {tokenHash,timestamp, u.getUserId()};
		
		try {
			conn=pool.checkOut();
			PreparedStatement ps=DAOUtil.prepareStatement(conn, UPDATE_TOKEN, false, values);
			ps.executeUpdate();
			
			if(ps.getUpdateCount()<1) {
				return null;
			}
			ps.close();
		}catch(Exception e) {
			e.printStackTrace();
			return null;
		}finally {
			pool.checkIn(conn);
		}
		return tokenVal;
	}
	
	public static User getByUsernameAndPassword(String username, String password) {
		Connection conn=null;
		User result=null;
		Object[] values= {username};
		try {
			conn=pool.checkOut();
			PreparedStatement ps=DAOUtil.prepareStatement(conn, GET_USER, false, values);

			ResultSet rs=ps.executeQuery();
			if(rs.next()) {
				result=new User(rs.getInt("userId"), rs.getString("username"),rs.getString("password"), rs.getString("token"), rs.getTimestamp("tokenExpiration"), rs.getBytes("salt"));
				String hashOfPass=hash(password, result.getSalt());
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
	
	
	
	private static String generateToken(String username) {
		/*Calendar c=Calendar.getInstance();
		int mins=c.get(Calendar.MINUTE);
		Random rand=new Random();
		int rndAdd=rand.nextInt(1000)+100;
		int rndChar=username.charAt(rand.nextInt(username.length()));
		
		int token= mins*rndChar+rndAdd;
		String tokenVal= String.valueOf(token);
		if(tokenVal.length()<4) {
			tokenVal+=rand.nextInt(10);
		}
		
		return tokenVal;*/
		LocalDateTime date = LocalDateTime.now();
		int seconds = date.toLocalTime().toSecondOfDay();
		Random rand=new Random();
		int rndAdd=rand.nextInt(90000)+10000;
		int rndChar=username.charAt(rand.nextInt(username.length()));
		
		String tokenVal= String.valueOf(seconds+rndAdd+rndChar);
		return tokenVal;
	}
	
	private static String hash(String token, byte[] salt) {
		byte[] dataBytes = token.getBytes();
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
