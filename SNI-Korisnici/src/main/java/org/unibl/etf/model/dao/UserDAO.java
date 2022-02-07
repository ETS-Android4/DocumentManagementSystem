package org.unibl.etf.model.dao;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Random;

import org.unibl.etf.model.dto.User;
import org.unibl.etf.util.ConnectionPool;
import org.unibl.etf.util.DAOUtil;

//crud nad userima
public class UserDAO {

	private static final ConnectionPool pool=ConnectionPool.getInstance();
	private static final String SELECT_ALL_FROM_USER="SELECT * FROM user";
	private static final String INSERT_USER="INSERT INTO user (username, password, role, permissions, ipAddress, rootDir, salt, mail) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
	private static final String GET_USER_BY_USERNAME="SELECT * FROM user WHERE username=? ";
	private static final String GET_USER_BY_USERNAME_AND_PASSWORD_AND_TOKEN="SELECT * FROM user WHERE username=? AND password=?";
	private static final String UPDATE_USER="UPDATE user SET username=?, password=?, role=?, permissions=?, ipAddress=?, tokenExpiration=?,  rootDir=? , mail=? WHERE userId=?";
	private static final String DELETE_USER="DELETE FROM user WHERE username=?";
	
	public static ArrayList<User> getAll(){
		Connection conn=null;
		ArrayList<User> result=new ArrayList<>();
		Object[] values= {};
		try {
			conn=pool.checkOut();
			PreparedStatement ps=DAOUtil.prepareStatement(conn, SELECT_ALL_FROM_USER, false, values);
			ResultSet rs=ps.executeQuery();
			while(rs.next()) {
				User u=new User(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6),rs.getString(7), rs.getTimestamp(8), rs.getString(9), rs.getBytes(10), rs.getString(11));
				result.add(u);
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
				result=new User(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6),rs.getString(7), rs.getTimestamp(8), rs.getString(9), rs.getBytes(10), rs.getString(11));
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
				result=new User(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6),rs.getString(7), rs.getTimestamp(8), rs.getString(9), rs.getBytes(10), rs.getString(11));
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
				result=new User(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6),rs.getString(7), rs.getTimestamp(8), rs.getString(9), rs.getBytes(10), rs.getString(11));
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
	
	public static boolean insert(User u) {
		Connection conn = null;
		boolean result=false;
		Random rand=new Random();
		byte[] salt=new byte[12];
	    rand.nextBytes(salt);
		
		u.setPassword(hashPassword(u.getPassword(), salt));
		u.setSalt(salt);
		
		Object[] values= {u.getUsername(), u.getPassword(), u.getRole(), u.getPermissions(), u.getIpAddress(), u.getRootDir(), u.getSalt(), u.getMail()};
		
		try {
			conn=pool.checkOut();
			PreparedStatement ps=DAOUtil.prepareStatement(conn, INSERT_USER, true, values);
			ps.executeUpdate();
			
			if(ps.getUpdateCount()>0) {
				result=true;
				try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
		            if (generatedKeys.next()) {
		                u.setUserId(generatedKeys.getInt(1));
		            }
		        }
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
	
	public static boolean update(User u, boolean changePass) {
		Connection conn = null;
		boolean result=false;	
		
		if(changePass) {
			//hesirati novi password sa starim saltom
			u.setPassword(hashPassword(u.getPassword(), u.getSalt()));
		}
		Object[] values={u.getUsername(), u.getPassword(), u.getRole(), u.getPermissions(), u.getIpAddress(), u.getTokenExpiration(), u.getRootDir(), u.getMail(),  u.getUserId()};
		
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
	
	public static boolean deleteUser(User u) {
		Connection conn = null;
		boolean result=false;		
		Object[] values= {u.getUsername()};
		
		try {
			conn=pool.checkOut();
			PreparedStatement ps=DAOUtil.prepareStatement(conn, DELETE_USER, false, values);
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
