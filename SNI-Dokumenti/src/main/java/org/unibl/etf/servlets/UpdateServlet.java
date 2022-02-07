package org.unibl.etf.servlets;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.util.ArrayList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import org.json.JSONObject;
import org.unibl.etf.model.beans.UserBean;
import org.unibl.etf.model.dao.HistoryRecordDAO;
import org.unibl.etf.model.dto.HistoryRecord;
import org.unibl.etf.util.Configuration;
import org.unibl.etf.util.Security;

/**
 * Servlet implementation class UpdateServlet
 */
@WebServlet("/UpdateServlet")
@MultipartConfig(fileSizeThreshold = 1024 * 1024,
maxFileSize = 1024 * 1024 * 5, 
maxRequestSize = 1024 * 1024 * 5 * 5)
public class UpdateServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UpdateServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session=request.getSession();
		UserBean userBean=(UserBean)session.getAttribute("userBean");
		String address="/WEB-INF/pages/login.jsp";
		String action=request.getParameter("action");
		
		if(action==null || "".equals(action)) {
			address="/WEB-INF/pages/error.jsp";
			RequestDispatcher dispatcher=request.getRequestDispatcher(address);
			dispatcher.forward(request, response);
		}else if(userBean==null || !userBean.isLoggedIn()) {
			address="/WEB-INF/pages/error.jsp";
			RequestDispatcher dispatcher=request.getRequestDispatcher(address);
			dispatcher.forward(request, response);
		}else if(userBean.getUser().getPermissions().contains("u")) {
			JSONObject json=null; 
			String relativePath=null;
		
		   String contentDisp ="";
		   try {
			   contentDisp= request.getPart("objArr").getHeader("content-disposition");
		   }catch(Exception e) {
			   response.setStatus(404);
			   return;
		   }
			  
			if(contentDisp!=null && contentDisp.contains("name=\"objArr\"")) {
				   //read additional data
				   InputStream is=request.getPart("objArr").getInputStream();
				   BufferedReader br=new BufferedReader(new InputStreamReader(is));
				   StringBuilder sb=new StringBuilder();
				   String line="";
				   while((line=br.readLine())!= null) {
					   sb.append(line);
				   }
				   is.close();
				   json=new JSONObject(sb.toString());
				   //checkJson
				   if(!json.has("parent") || !json.has("dest")) {
					   response.setStatus(404);
					   return;
				   }
				   
				   relativePath=json.getString("parent")+ json.getString("dest");
				   
				   if(relativePath.contains("..")) {
					   response.setStatus(404);
					   return;
				   }
				   
				   
				   if("".equals(json.getString("parent"))) {
					   if(!userBean.getUser().getRootDir().equals(json.getString("dest"))) {
						   response.setStatus(404);
						   return;
					   }
				   }else {
					   String rootDir=userBean.getUser().getRootDir()+"/";
					   if(!relativePath.startsWith(rootDir)) {
						   response.setStatus(404);
						   return;
					   }
				   }
				  		   
	            }else {
	            	//destination not sent
	            	response.setStatus(404);
	            	return;
	            }
			   String destination=Configuration.rootDir+ relativePath;
			   //check if file exists
			   ArrayList<Part> parts=(ArrayList<Part>) request.getParts();
			   if(parts.size()<2) {
				   response.setStatus(404);
				   return;
			   }
			   String filename=parts.get(1).getSubmittedFileName();
			   
			   File f=new File(destination+ File.separator+ filename);
			   if(!f.exists()) {
				   response.setStatus(404);
				   return;
			   }
			   for (Part part : request.getParts()) {
				   String cd = part.getHeader("content-disposition");
				   if(!cd.contains("name=\"objArr\"")) {
					   String fileName = part.getSubmittedFileName();
					   part.write(destination + File.separator + fileName);
				   } 
			   }
			   Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		       HistoryRecordDAO.insert(new HistoryRecord(0, timestamp, userBean.getUser().getUsername(), "update" , filename));

		}else {
			//user can't update
			response.setStatus(404);
			return;
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
