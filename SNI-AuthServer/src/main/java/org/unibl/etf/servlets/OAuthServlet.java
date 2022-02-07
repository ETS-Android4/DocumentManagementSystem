package org.unibl.etf.servlets;

import java.io.IOException;

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
import org.unibl.etf.util.IdTokenVerifier;
import org.unibl.etf.util.TicketsUtil;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;

/**
 * Servlet implementation class OAuthServlet
 */
@WebServlet("/OAuthServlet")
public class OAuthServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public OAuthServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String address="/WEB-INF/pages/login_d.jsp";
		String idToken = request.getParameter("id_token");
		HttpSession session=request.getSession();
		
		GoogleIdToken.Payload payload;
		try {
			payload = IdTokenVerifier.getPayload(idToken);
			
			if(payload==null) {
				session.setAttribute("notification", "Bad authentication");
			}else {
				 String email = payload.getEmail();
			
				 User currUser=UserDAO.getByMail(email);
				 if(currUser!=null) {	
					session.invalidate();
					request.getSession(true);
					session=request.getSession();
					
					UserBean uBean=new UserBean();
					uBean.setUser(currUser);
					
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
						
						response.sendRedirect(Configuration.urlStartDocs+"SNI-Dokumenti/documents?action=log&ticket="+ticket);
						return;
					}else if("K".equals(uBean.getUser().getRole()) && !currUser.getIpAddress().equals(request.getRemoteAddr())) {
						address="/WEB-INF/pages/login_d.jsp";
						session.setAttribute("notification", "Login not possible");
					}else {
						response.sendRedirect(Configuration.urlStartDocs+"SNI-Dokumenti/documents?action=log&ticket="+ticket);
						return;
					}
				 }else {
					 session.setAttribute("notification", "Google authentication failed");
				 }
			}
		} catch (Exception e) {
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
