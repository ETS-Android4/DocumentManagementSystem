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
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.unibl.etf.model.beans.UserBean;
import org.unibl.etf.model.dao.FileDAO;
import org.unibl.etf.model.dao.UserDAO;
import org.unibl.etf.model.dto.File;
import org.unibl.etf.model.dto.User;
import org.unibl.etf.util.Configuration;
import org.unibl.etf.util.Security;

import com.google.gson.Gson;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;

/**
 * Servlet implementation class LoginServlet
 */
@WebServlet("/users")
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
		UserBean uBean=(UserBean)session.getAttribute("userBean");
	
		String address="/WEB-INF/pages/error.jsp";
		String action=request.getParameter("action");
		
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
						UserBean userBean=new UserBean();
						userBean.setLoggedIn(true);
						userBean.setUser(user);
						
						session.setAttribute("userBean", userBean);
						
						ArrayList<User> users=UserDAO.getAll();
						session.setAttribute("users", users);

						session.setAttribute("ticket", ticket);
						session.setAttribute("jwt", jwt.toString());
						
						address="/WEB-INF/pages/main_page.jsp";	
					}
				}
			}
		}else if(uBean==null || !uBean.isLoggedIn()) { //must be logged for all other actions
			response.sendRedirect(Configuration.urlStartAuth+"SNI-AuthServer/auth?action=documents");
			return;
		}else if("logout".equals(action)) {
			session.invalidate();
			response.sendRedirect(Configuration.urlStartAuth+"SNI-AuthServer/logout?action=users");
			return;
		}else if("add".equals(action)) {
			address="/ActionsServlet";
		}else if("delete".equals(action)) {
			address="/ActionsServlet";
		}else if("update".equals(action)) {
			address="/ActionsServlet";
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
