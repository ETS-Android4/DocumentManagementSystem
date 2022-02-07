package org.unibl.etf.service;

import org.json.JSONObject;
import org.unibl.etf.model.dao.UserDAO;
import org.unibl.etf.model.dto.User;

public class TokenGeneratorService {

	public static String generateToken(String object) {
		JSONObject jObj=new JSONObject(object);
		
		if(!jObj.has("username") || !jObj.has("password")) {
			return null;
		}
		String username=jObj.getString("username");
		String password=jObj.getString("password");
		
		User user=UserDAO.getByUsernameAndPassword(username, password);
		if(user!=null) {
			String token=UserDAO.updateToken(user);
			System.out.println(token);
			return token;
		}else {
			return null;
		}
	}

}
