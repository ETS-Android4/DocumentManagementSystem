package org.unibl.etf.model.beans;

import java.sql.Timestamp;
import java.util.ArrayList;

import org.unibl.etf.model.dao.HistoryRecordDAO;
import org.unibl.etf.model.dto.HistoryRecord;

public class HistoryRecordBean {

	public ArrayList<HistoryRecord> getAll(){
		return HistoryRecordDAO.getAll();
	}

}
