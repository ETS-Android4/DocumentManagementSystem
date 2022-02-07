package org.unibl.etf.servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;
import org.unibl.etf.model.beans.UserBean;
import org.unibl.etf.model.dao.FileDAO;
import org.unibl.etf.model.dao.UserDAO;
import org.unibl.etf.model.dto.File;
import org.unibl.etf.model.dto.User;
import org.unibl.etf.util.Security;

/**
 * Servlet implementation class ActionsServlet
 */
@WebServlet("/ActionsServlet")
public class ActionsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ActionsServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setHeader("Content-Security-Policy", Security.CSP_POLICY);
		String action=request.getParameter("action");
		String address="/WEB-INF/pages/login.jsp";
		HttpSession session=request.getSession();
		UserBean uBean=(UserBean)session.getAttribute("userBean");	
		
		if(action==null || "".equals(action)) {
			address="/WEB-INF/pages/error.jsp";
			RequestDispatcher dispatcher=request.getRequestDispatcher(address);
			dispatcher.forward(request, response);
		}
		else if(uBean==null || !uBean.isLoggedIn()) {
			address="/WEB-INF/pages/error.jsp";
			RequestDispatcher dispatcher=request.getRequestDispatcher(address);
			dispatcher.forward(request, response);
		}else if("add".equals(action)) {
				String jsonBody = new BufferedReader(new InputStreamReader(request.getInputStream())).lines().collect(
			            Collectors.joining("\n"));
			    if (jsonBody == null || jsonBody.trim().length() == 0) {
			        response.setStatus(404);
			    }else {
			    	JSONObject obj=new JSONObject(jsonBody);
			    	if(!Security.checkJsonForUser(obj)) {
			    		response.setStatus(404);
			    		return;
			    	}
			    	
			    	String username=obj.getString("username");
					String password=obj.getString("password");
					String mail=obj.getString("mail");
					String role=obj.getString("role");
					String permissions="crud";
					
					String rootDir=null;
					String ipAddress=null;
					if("K".equals(role)) {
						ipAddress=obj.getString("ipAddress");
						String create=(obj.getBoolean("create")?"c":"");
						String read=(obj.getBoolean("read")?"r":"");
						String update=(obj.getBoolean("update")?"u":"");
						String delete=(obj.getBoolean("delete")?"d":"");
						permissions=create+read+update+delete;
					}
					
					if("A".equals(role)) {
						rootDir="root";
					}else {
						rootDir=obj.getString("rootDir");
					}
					
					User newUser=new User(0, username, password, role, permissions, ipAddress, rootDir, mail);
					if(!Security.isValidInput(newUser)) {
						System.out.println("Malicious input:"+newUser);
						response.setStatus(404);
						return;
					}
					
					int fileResult=FileDAO.createIfNotExists(rootDir);
					
					if(fileResult<0 || !UserDAO.insert(newUser)) {
						response.setStatus(404);
					}
			    }
		 }else if("delete".equals(action) ) {
			 String jsonBody = new BufferedReader(new InputStreamReader(request.getInputStream())).lines().collect(
			            Collectors.joining("\n"));
			 if (jsonBody == null || jsonBody.trim().length() == 0) {
		        response.setStatus(404);
			 }else {
				 JSONObject obj=new JSONObject(jsonBody);
				 if(!obj.has("username")) {
					 response.setStatus(404);
					 return;
				 }
				 String username=obj.getString("username");
		 	    	
			   	 User userForDel=new User();
				 userForDel.setUsername(username);
				 
				 //can't delete himself
				 if(username.equals(uBean.getUser().getUsername()) || !UserDAO.deleteUser(userForDel)) {
					 response.setStatus(404);
				 } 
			 }
		 }else if("update".equals(action)) {
			 String jsonBody = new BufferedReader(new InputStreamReader(request.getInputStream())).lines().collect(
			            Collectors.joining("\n"));
			 if (jsonBody == null || jsonBody.trim().length() == 0) {
		        response.setStatus(404);
			 }else {
				JSONObject obj=new JSONObject(jsonBody);
				if(!Security.checkJsonForUser(obj) || !obj.has("prevUsername")) {
					response.setStatus(404);
					return;
				}
				
				String prevUsername=obj.getString("prevUsername");
				 
				String username=obj.getString("username");
				String password=obj.getString("password");
				String mail=obj.getString("mail");
				String role=obj.getString("role");
				String permissions="crud";
				
				String rootDir=null;
				String ipAddress=null;
				if("K".equals(role)) {
					ipAddress=obj.getString("ipAddress");
					String create=(obj.getBoolean("create")?"c":"");
					String read=(obj.getBoolean("read")?"r":"");
					String update=(obj.getBoolean("update")?"u":"");
					String delete=(obj.getBoolean("delete")?"d":"");
					permissions=create+read+update+delete;
				}
				
				if("A".equals(role)) {
					rootDir="root";
				}else {
					rootDir=obj.getString("rootDir");
				}
				
				User oldUser=UserDAO.getByUsername(prevUsername);
				if(oldUser!=null) {
					int oldUserId=oldUser.getUserId();
					User updatedUser=new User(oldUserId, username, password, role, permissions, ipAddress, rootDir, mail);
					
					if(!Security.isValidInput(updatedUser)) {
						System.out.println("Malicious input:"+updatedUser);
						response.setStatus(404);
						return;
					}
					
					//tokenExp and salt stay same
					updatedUser.setTokenExpiration(oldUser.getTokenExpiration());
					updatedUser.setSalt(oldUser.getSalt());
					
					int fileResult=FileDAO.createIfNotExists(rootDir);
					if(fileResult<0) {
						response.setStatus(404);
						return;
					}
					
					boolean mailChange=(mail.length()==0?false:true);
					if(!mailChange) {
						updatedUser.setMail(oldUser.getMail());
					}
					
					boolean passChange=((password==null || password.length()==0)?false:true);
					if(!passChange) {
						updatedUser.setPassword(oldUser.getPassword());
					}
					if(!UserDAO.update(updatedUser, passChange)) {
						response.setStatus(404);
					}
				}
			 }
		 }
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
