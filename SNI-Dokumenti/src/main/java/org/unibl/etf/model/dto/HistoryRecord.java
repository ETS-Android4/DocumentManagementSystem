package org.unibl.etf.model.dto;

import java.sql.Timestamp;
import java.util.Date;

public class HistoryRecord {

	private int recordId;
	private Timestamp dateTime;
	private String username;
	private String action;
	private String filename;
	
	public HistoryRecord() {
		// TODO Auto-generated constructor stub
	}

	public HistoryRecord(int recordId,  Timestamp dateTime,String username, String action, String filename) {
		super();
		this.recordId = recordId;
		this.username = username;
		this.dateTime = dateTime;
		this.action = action;
		this.filename = filename;
	}

	public int getRecordId() {
		return recordId;
	}

	public void setRecordId(int recordId) {
		this.recordId = recordId;
	}
	

	public String getUsername() {
		return username;
	}



	public void setUsername(String username) {
		this.username = username;
	}



	public Timestamp getDateTime() {
		return dateTime;
	}

	public void setDateTime(Timestamp dateTime) {
		this.dateTime = dateTime;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	
}
