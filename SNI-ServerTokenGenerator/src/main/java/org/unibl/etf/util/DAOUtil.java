package org.unibl.etf.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class DAOUtil {

	public static PreparedStatement prepareStatement(Connection connection,
			String sql, boolean returnGeneratedKeys, Object... values)
			throws SQLException {
		PreparedStatement preparedStatement = connection.prepareStatement(sql,
				returnGeneratedKeys ? Statement.RETURN_GENERATED_KEYS
						: Statement.NO_GENERATED_KEYS);
		setValues(preparedStatement, values);
		return preparedStatement;
	}

	public static void setValues(PreparedStatement preparedStatement,
			Object... values) throws SQLException {
		for (int i = 0; i < values.length; i++) {
			preparedStatement.setObject(i + 1, values[i]);
		}
	}

}

/*
 * 
 * 
 * 	public static void main(String[] args) {
			byte[] dataBytes = "admin".getBytes();
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(dataBytes);
			byte[] digest = md.digest();
		
			System.out.println(byteArrayToHex(digest));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}
	public static String byteArrayToHex(byte[] a) {
   StringBuilder sb = new StringBuilder(a.length * 2);
   for(byte b: a)
      sb.append(String.format("%02x", b));
   return sb.toString();
}*/
