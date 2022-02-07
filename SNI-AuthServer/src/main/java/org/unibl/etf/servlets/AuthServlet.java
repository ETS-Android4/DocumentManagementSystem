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
import org.unibl.etf.util.Configuration;
import org.unibl.etf.util.TicketsUtil;

/**
 * Servlet implementation class AuthServlet
 */
@WebServlet("/auth")
public class AuthServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AuthServlet() {
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
			RequestDispatcher dispatcher=request.getRequestDispatcher(address);
			dispatcher.forward(request, response);
			return;
		}else if("login_d".equals(action) || "second_login_d".equals(action) || "login_u".equals(action) || "second_login_u".equals(action) ) {
			address="/login";
			RequestDispatcher dispatcher=request.getRequestDispatcher(address);
			dispatcher.forward(request, response);
			return;
		}else if("auth".equals(action)) {
			address="/OAuthServlet";
			RequestDispatcher dispatcher=request.getRequestDispatcher(address);
			dispatcher.forward(request, response);
			return;
		}
		else if(!"users".equals(action) && !"documents".equals(action)) {			
			address="/WEB-INF/pages/error.jsp";
			RequestDispatcher dispatcher=request.getRequestDispatcher(address);
			dispatcher.forward(request, response);
			return;
		}
		
		boolean cookieFound=false;
		Cookie currCookie=null;
		Cookie[] cookies=request.getCookies();
	
		if(session.getAttribute("ticket")!=null && cookies!=null) {
			for(Cookie c:cookies) {
				if(c.getName().equals("TICKET")) {					
					//check if cookie is valid
					String ticket=(String)session.getAttribute("ticket");
					if(ticket.equals(c.getValue())) {
						currCookie=c;
						cookieFound=true;
					}
				}
			}	
		}
		
		if(!cookieFound) {						
			if("users".equals(action)) {
				address="/WEB-INF/pages/login_u.jsp";
			}else if("documents".equals(action)) {
				address="/WEB-INF/pages/login_d.jsp";
			}
			
			RequestDispatcher dispatcher=request.getRequestDispatcher(address);
			dispatcher.forward(request, response);
			return;
		}else {
			UserBean uBean=(UserBean)session.getAttribute("userBean");
			//get new ticket
			String ticketForAnotherApp=TicketsUtil.getTicket();
			
			synchronized(TicketsUtil.mapLocker) {
				TicketsUtil.tickets.put(ticketForAnotherApp, uBean.getUser());
			}
			
			if("users".equals(action)) {
				response.sendRedirect(Configuration.urlStartUsers+"SNI-Korisnici/users?action=log&ticket="+ticketForAnotherApp);
			}else if("documents".equals(action)) {
				response.sendRedirect(Configuration.urlStartDocs+"SNI-Dokumenti/documents?action=log&ticket="+ticketForAnotherApp);
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
