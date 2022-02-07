package org.unibl.etf.servlets;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.unibl.etf.model.beans.UserBean;
import org.unibl.etf.util.Configuration;

import com.google.gson.Gson;

/**
 * Servlet Filter implementation class SSOFilter
 */
@WebFilter("/SSOFilter")
public class SSOFilter implements Filter {

    /**
     * Default constructor. 
     */
    public SSOFilter() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
	    HttpServletResponse res = (HttpServletResponse) response;
		HttpSession session=req.getSession();
		String action=req.getParameter("action");
		
		UserBean uBean=(UserBean)session.getAttribute("userBean");
		if(uBean!=null && "A".equals(uBean.getUser().getRole()) && !"logout".equals(action)) {
			
			URL url = new URL(Configuration.urlStartAuth+"SNI-AuthServer/check");
		    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		    conn.setRequestMethod("POST");
		    conn.setDoOutput(true);
		    conn.setRequestProperty("Content-Type", "application/json");
			
		    OutputStream os = conn.getOutputStream();
		    HashMap<String, String> map=new HashMap<String, String>();
		    map.put("username", uBean.getUser().getUsername());
		    Gson gson=new Gson();
		    
		    os.write(gson.toJson(map).getBytes());
		    os.flush();
		    os.close();
		    conn.connect();
		    

		    if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
		    	chain.doFilter(request, response);
		    }else {
		    	res.setStatus(401);
		    }
		    
		    conn.disconnect();
		}else {
			chain.doFilter(request, response);
		}
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
	}

}
