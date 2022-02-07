package org.unibl.etf.model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;

import org.unibl.etf.model.dto.HistoryRecord;
import org.unibl.etf.util.ConnectionPool;
import org.unibl.etf.util.DAOUtil;

public class HistoryRecordDAO {

	private static ConnectionPool pool=ConnectionPool.getInstance();
	private static final String SELECT_ALL_RECORDS="SELECT * FROM history_record";
	private static final String INSERT="INSERT INTO history_record (dateTime, username, action, fileName) VALUES (?, ?, ?, ?)";
	private static final String SELECT_ALL_AFTER_LOGOUT="SELECT * FROM history_record where (action='delete' or action='deleteDir') and dateTime>?";
	
	public static ArrayList<HistoryRecord> getAll(){
		ArrayList<HistoryRecord> result=new ArrayList<HistoryRecord>();
		Connection conn=null;
		Object[] values= {};
		
		try {
			conn=pool.checkOut();
			PreparedStatement ps=DAOUtil.prepareStatement(conn, SELECT_ALL_RECORDS, false, values);
			ResultSet rs=ps.executeQuery();
			
			while(rs.next()) {
				HistoryRecord hr=new HistoryRecord(rs.getInt(1), rs.getTimestamp(2), rs.getString(3), rs.getString(4), rs.getString(5));
				result.add(hr);
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
	

	
	public static boolean insert(HistoryRecord hr) {
		boolean result=false;
		Connection conn=null;
		Object[] values= {hr.getDateTime(), hr.getUsername(), hr.getAction(), hr.getFilename()};
		
		try {
			conn=pool.checkOut();
			PreparedStatement ps=DAOUtil.prepareStatement(conn, INSERT, true, values);
			ps.executeUpdate();
			
			if(ps.getUpdateCount()>0) {
				result=true;
				try(ResultSet generatedKeys=ps.getGeneratedKeys()){
					if(generatedKeys.next()) {
						hr.setRecordId(generatedKeys.getInt(1));
					}
				}
			}
			ps.close();
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			pool.checkIn(conn);
		}
		return result;
	}

}
