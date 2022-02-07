package org.unibl.etf.model.dao;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import org.unibl.etf.util.Configuration;
import org.unibl.etf.util.ConnectionPool;
import org.unibl.etf.util.DAOUtil;

public class FileDAO {
	
	public static int createIfNotExists(String rootDir) {
		if("root".equals(rootDir)) {
			return 0;
		}
		File newRoot=new File(Configuration.rootDir+rootDir);
		
		if(!newRoot.exists()) {
			 return newRoot.mkdirs()?1:-1;
		}
		return 0;
	}

}
