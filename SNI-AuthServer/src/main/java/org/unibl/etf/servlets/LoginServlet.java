package org.unibl.etf.servlets;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.unibl.etf.model.beans.UserBean;
import org.unibl.etf.model.dao.UserDAO;
import org.unibl.etf.model.dto.User;
import org.unibl.etf.util.Configuration;
import org.unibl.etf.util.TicketsUtil;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * Servlet implementation class LoginServlet
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoginServlet() {
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
		session.setAttribute("notification", "");
		if(action==null || "".equals(action)) {
			address="/WEB-INF/pages/error.jsp";
		}else if("login_u".equals(action)) { 
			String username=request.getParameter("username");
			String password=request.getParameter("password");

			User currUser=UserDAO.getByUsernameAndPassword(username, password);
			if(currUser!=null && "A".equals(currUser.getRole())) {
				UserBean uBean=new UserBean();
				uBean.setUser(currUser);
				
				//SESION FIXATION
				session.invalidate();
				request.getSession(true);
				session=request.getSession();
				
				session.setAttribute("userBean", uBean);
				
				address="/WEB-INF/pages/second_login_u.jsp";
	
			}else {
				address="/WEB-INF/pages/login_u.jsp";
				session.setAttribute("notification", "Incorrect username or password");
			}
		}else if("second_login_u".equals(action)) {			
			UserBean uBean=(UserBean)session.getAttribute("userBean");
			
			//was on frist auth page
			if(uBean!=null) {
				String token=request.getParameter("token");
				
				if(uBean.checkToken(token)) {
					session.invalidate();
					request.getSession(true);
					session=request.getSession();
					session.setAttribute("userBean", uBean);
					
					String ticket=TicketsUtil.getTicket();
					synchronized(TicketsUtil.mapLocker) {
						//for REST
						TicketsUtil.tickets.put(ticket, uBean.getUser());
					}
					TicketsUtil.addUsername(uBean.getUser().getUsername());
					
					Cookie c=new Cookie("TICKET", ticket);
					session.setAttribute("ticket",  ticket);
					c.setMaxAge(60*60);
					c.setHttpOnly(true);
					response.addCookie(c);
					response.sendRedirect(Configuration.urlStartUsers+"SNI-Korisnici/users?action=log&ticket="+ticket);
					return;
					
				}else {
					session.invalidate();
					address="/WEB-INF/pages/login_u.jsp";
				}
			}
		}else if("login_d".equals(action)) {
			String username=request.getParameter("username");
			String password=request.getParameter("password");
			
			User currUser=UserDAO.getByUsernameAndPassword(username, password);
			if(currUser!=null) {
				
				if(currUser.getRole().equals("K") && !currUser.getIpAddress().equals(request.getRemoteAddr())) {
					address="/WEB-INF/pages/login_d.jsp";
					session.setAttribute("notification", "Login not possible");
				}else {
					UserBean uBean=new UserBean();
					uBean.setUser(currUser);
					
					//SESSION FIXATION ODBRANA
					session.invalidate();
					request.getSession(true);
					session=request.getSession();
					
					session.setAttribute("userBean", uBean);
					
					address="/WEB-INF/pages/second_login_d.jsp";
				}
			}else {
				address="/WEB-INF/pages/login_d.jsp";
				session.setAttribute("notification", "Incorrect username or password");
			}
		}else if("second_login_d".equals(action)) {
			UserBean uBean=(UserBean)session.getAttribute("userBean");
			
			//was on first auth
			if(uBean!=null) {
				String token=request.getParameter("token");
				
				if(uBean.checkToken(token)) {
					session.invalidate();
					request.getSession(true);
					session=request.getSession();
					session.setAttribute("userBean", uBean);
					
					String ticket=TicketsUtil.getTicket();
					synchronized(TicketsUtil.mapLocker) {
						//for REST
						TicketsUtil.tickets.put(ticket, uBean.getUser());
					}
					TicketsUtil.addUsername(uBean.getUser().getUsername());
				
				
					if("A".equals(uBean.getUser().getRole())) {
						Cookie c=new Cookie("TICKET",ticket);
						session.setAttribute("ticket",  ticket);
						c.setMaxAge(60*60);
						c.setHttpOnly(true);
						response.addCookie(c);
					}
					response.sendRedirect(Configuration.urlStartDocs+"SNI-Dokumenti/documents?action=log&ticket="+ticket);
					return;
					
				}else {
					session.invalidate();
					address="/WEB-INF/pages/login_d.jsp";
				}
			}
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
