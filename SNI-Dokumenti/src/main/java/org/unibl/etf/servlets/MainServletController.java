package org.unibl.etf.servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;
import org.unibl.etf.model.beans.FileBean;
import org.unibl.etf.model.beans.HistoryRecordBean;
import org.unibl.etf.model.beans.UserBean;
import org.unibl.etf.model.dao.UserDAO;
import org.unibl.etf.model.dto.HistoryRecord;
import org.unibl.etf.model.dto.User;
import org.unibl.etf.util.Configuration;
import org.unibl.etf.util.FileSystem;
import org.unibl.etf.util.Security;

import com.google.gson.Gson;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

/**
 * Servlet implementation class LoginServlet
 */
@WebServlet("/documents")
@MultipartConfig(fileSizeThreshold = 1024 * 1024,
maxFileSize = 1024 * 1024 * 5, 
maxRequestSize = 1024 * 1024 * 5 * 5)
public class MainServletController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MainServletController() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session=request.getSession();
		String action=request.getParameter("action");
		String address="/WEB-INF/pages/error.jsp";
		
		UserBean uBean=(UserBean)session.getAttribute("userBean");
		if(action==null || "".equals(action)) {
			address="/WEB-INF/pages/error.jsp";
		}
		else if("log".equals(action)) {
			//comes from auth server
			String ticket=request.getParameter("ticket");
			
			if(ticket!=null) {
				//validate ticket
				URL url = new URL(Configuration.urlStartAuth+"SNI-AuthServer/tickets");
			    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			    conn.setRequestMethod("POST");
			    conn.setDoOutput(true);
			    conn.setRequestProperty("Content-Type", "application/json");
				
			    OutputStream os = conn.getOutputStream();
			    HashMap<String, String> map=new HashMap<String, String>();
			    map.put("ticket", ticket);
			    Gson gson=new Gson();
			    
			    os.write(gson.toJson(map).getBytes());
			    os.flush();
			    os.close();
			    conn.connect();
			    
			    if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
			    	
					BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
					StringBuilder jwt=new StringBuilder();
					String line="";
					while ((line = br.readLine()) != null) {
						jwt.append(line);
					}
					
					PublicKey publicKey=null;
					try {
						publicKey = Security.getPublicKey();
					} catch (NoSuchAlgorithmException | InvalidKeySpecException | IOException e) {
						System.out.println("public key exception");
					}
					Claims claims=null;
					
					try{
						claims=Jwts.parser().setSigningKey(publicKey).parseClaimsJws(jwt.toString()).getBody();
					}catch(Exception e) {
						System.out.println("jwt not good");
						response.setStatus(404);
						return;
					}
					
					String username=(String) claims.get("username");
				
					User user=UserDAO.getByUsername(username);
					
					if(user!=null) {
						//SESSION FIXATION
						session.invalidate();
						request.getSession(true);
						session=request.getSession();
						
						UserBean userBean=new UserBean();
						userBean.setLoggedIn(true);
						userBean.setUser(user);
						
						session.setAttribute("userBean", userBean);
						
						if("A".equals(userBean.getUser().getRole())) {
							HistoryRecordBean recordsBean=new HistoryRecordBean();
							session.setAttribute("recordsBean", recordsBean);
						
							address="/WEB-INF/pages/main_page_admin.jsp";
						}else if("K".equals(userBean.getUser().getRole())) {
							address="/WEB-INF/pages/main_page_basic.jsp";
						}else if("AD".equals(userBean.getUser().getRole())) {
							address="/WEB-INF/pages/main_page_advanced.jsp";
						}
			
						session.setAttribute("ticket", ticket);
						session.setAttribute("jwt", jwt.toString());
					
					}else {
						address="/WEB-INF/pages/error.jsp";	
					}	
					
				}else {
					address="/WEB-INF/pages/error.jsp";	
				}
			    
			    conn.disconnect();
			}else {
				address="/WEB-INF/pages/error.jsp";	
			}	
		}else if(uBean==null || !uBean.isLoggedIn()) { //must be logged for all other actions
			response.sendRedirect(Configuration.urlStartAuth+"SNI-AuthServer/auth?action=documents");
			return;
		}
		else if("logout".equals(action)) {
			session.invalidate();
			response.sendRedirect(Configuration.urlStartAuth+"SNI-AuthServer/logout?action=documents");
			return;
		}else if("list".equals(action) || "download".equals(action) || "delete".equals(action) || "history".equals(action)) {
			address="/ActionsServlet";
		}else if("upload".equals(action)) {
			address="/UploadServlet";
		}else if("update".equals(action)) {
			address="/UpdateServlet";
		}else if("createDir".equals(action) || "deleteDir".equals(action) || "move".equals(action)){
			address="/AdditionalActionsServlet";
		}else {
			address="/WEB-INF/pages/error.jsp";
		}
		
		RequestDispatcher dispatcher=request.getRequestDispatcher(address);
		dispatcher.forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
