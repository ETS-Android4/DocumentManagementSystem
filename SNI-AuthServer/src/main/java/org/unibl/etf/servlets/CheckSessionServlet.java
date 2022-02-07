package org.unibl.etf.servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;
import org.unibl.etf.util.TicketsUtil;

/**
 * Servlet implementation class CheckSessionServle
 */
@WebServlet("/check")
public class CheckSessionServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CheckSessionServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {				
		String jsonBody = new BufferedReader(new InputStreamReader(request.getInputStream())).lines().collect(
	            Collectors.joining("\n"));
		
		if(jsonBody!=null && !"".equals(jsonBody)) {
			JSONObject obj=new JSONObject(jsonBody);
			if(!obj.has("username")){
				response.setStatus(404);
				return;
			}
			
			boolean res=TicketsUtil.doesContain(obj.getString("username"));
			if(!res) {
				response.setStatus(404);
			}else {
				response.setStatus(200);
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
