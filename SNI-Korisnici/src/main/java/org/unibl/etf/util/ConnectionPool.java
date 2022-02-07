package org.unibl.etf.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class ConnectionPool  {
	
	private static ConnectionPool instance=null;
	private int preConnectCount;
	private int maxIdleConnections;
	private int maxConnections;
	private int connectCount;
	private String jdbcUrl;
	private String username;
	private String password;
	private ArrayList<Connection> freeConnections;
	private ArrayList<Connection> usedConnections;
	

	public static ConnectionPool getInstance() {
		if(instance==null) {
			instance=new ConnectionPool();
		}
		return instance;
	}
	
	private ConnectionPool() {
		readConfig();
		
		try {
			freeConnections=new ArrayList<>();
			usedConnections=new ArrayList<>();
			
			for(int i=0; i<preConnectCount; i++) {
				Connection conn=DriverManager.getConnection(jdbcUrl, username, password);
				conn.setAutoCommit(true);
				freeConnections.add(conn);
			}
			connectCount=preConnectCount;
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void readConfig() {
		ResourceBundle bundle=PropertyResourceBundle.getBundle("org.unibl.etf.util.ConnectionPool");
		jdbcUrl=bundle.getString("jdbcUrl");
		username=bundle.getString("username");
		password=bundle.getString("password");
		String driver=bundle.getString("driver");
		preConnectCount=0;
		maxIdleConnections=10;
		maxConnections=10;
		
		try {
			Class.forName(driver);
			preConnectCount=Integer.parseInt(bundle.getString("preConnectCount"));
			maxIdleConnections=Integer.parseInt(bundle.getString("maxIdleConnections"));
			maxConnections=Integer.parseInt(bundle.getString("maxConnections"));
		}catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public synchronized Connection checkOut() throws SQLException {
		Connection conn=null;
		
		if(freeConnections.size()>0) {
			conn=(Connection)freeConnections.get(0);
			freeConnections.remove(0);
			usedConnections.add(conn);
		}else {
			if(connectCount<maxConnections) {
				conn=DriverManager.getConnection(jdbcUrl, username, password);
				connectCount++;
				usedConnections.add(conn);
			}else {
				try {
					wait();
					
					conn=freeConnections.get(0);
					freeConnections.remove(0);
					usedConnections.add(conn);
				}catch(InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		return conn;
	}
	
	public synchronized void checkIn(Connection conn) {
		if(conn==null) {
			return;
		}
		if(usedConnections.remove(conn)) {
			freeConnections.add(conn);
			if(freeConnections.size()>maxIdleConnections) {
				int lastOne=freeConnections.size()-1;
				Connection c=freeConnections.get(lastOne);
				try {
					c.close();
				}catch(SQLException e) {
					e.printStackTrace();
				}
				freeConnections.remove(lastOne);
			}
			notify();
		}
	}

}