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
 * Servlet implementation class LogoutServlet
 */
@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LogoutServlet() {
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
	
		
		if(!"users".equals(action) && !"documents".equals(action)) {			
			address="/WEB-INF/pages/error.jsp";
			RequestDispatcher dispatcher=request.getRequestDispatcher(address);
			dispatcher.forward(request, response);
			return;
		}
		 
		boolean cookieFound=false;
		Cookie currCookie=null;
		Cookie[] cookies=request.getCookies();
		UserBean uBean=(UserBean)session.getAttribute("userBean");
		
		if(cookies!=null) {
			for(Cookie c:cookies) {
				if(c.getName().equals("TICKET")) {
					//check if cookie is valid
					String ticket=(String)session.getAttribute("ticket");
					if(ticket!=null && ticket.equals(c.getValue())) {
						
						currCookie=c;
						cookieFound=true;
					}
				}
			}	
		}
			
			
		/*if(cookieFound) {
			//remove session attribute if exists
			session.removeAttribute("ticket");
		}*/
		session.invalidate();
		
		if(uBean!=null) {
			//first logout
			TicketsUtil.removeUsername(uBean.getUser().getUsername());
		}

		//redirect to login
		if("users".equals(action)) {
			response.sendRedirect(Configuration.urlStartAuth+"SNI-AuthServer/auth?action=users");
		}else if("documents".equals(action)) {
			response.sendRedirect(Configuration.urlStartAuth+"SNI-AuthServer/auth?action=documents");
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
