package org.unibl.etf.servlets;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.stream.Collectors;

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
import org.unibl.etf.model.dao.HistoryRecordDAO;
import org.unibl.etf.model.dto.HistoryRecord;
import org.unibl.etf.util.FileSystem;
import org.unibl.etf.util.MailSender;
import org.unibl.etf.util.Security;

import com.google.gson.Gson;

/**
 * Servlet implementation class FileSystemTest
 */

@WebServlet("/ActionsServlet")
public class ActionsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String ROOT="root/";
       
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
		}else if("list".equals(action)) {
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			
			FileBean file=FileSystem.traverseDirectory(userBean.getUser().getRootDir());
			Gson gson=new Gson();
			String json=gson.toJson(file);
			
			PrintWriter out=response.getWriter();
			out.write(json);
			out.flush();
			out.close();
		}else if("download".equals(action) || "delete".equals(action)) {
			String jsonBody = new BufferedReader(new InputStreamReader(request.getInputStream())).lines().collect(
		            Collectors.joining("\n"));
			if(jsonBody==null || jsonBody.trim().length()==0) {
				response.setStatus(404);
			}else {
				JSONObject obj=new JSONObject(jsonBody);
			
				 if("download".equals(action) && userBean.getUser().getPermissions().contains("r") && obj.has("parent") && obj.has("filename")){					
					String parent=obj.getString("parent");
					File fileForDownload=null;
					if("K".equals(userBean.getUser().getRole()) || "AD".equals(userBean.getUser().getRole())) {
						String rootDir=userBean.getUser().getRootDir()+"/";
						String relativePath=parent+ obj.getString("filename");
						if(parent.startsWith(rootDir) && !relativePath.contains("..")) {						
							fileForDownload=FileSystem.getFile(relativePath);						
						}else {
							response.setStatus(404);
						}
					}else {						
						String relativePath=parent.substring(ROOT.length())+ obj.getString("filename");
						fileForDownload=FileSystem.getFile(relativePath);
					}
					
					if(fileForDownload!=null) {
						String mimeType = getServletContext().getMimeType(fileForDownload.getAbsolutePath());
						 if (mimeType == null) {   
					            mimeType = "application/octet-stream";
					     }

						 response.setContentType(mimeType);
						 response.setContentLength((int)fileForDownload.length());
						 
						//forse download
						String headerKey = "Content-Disposition";
				        String headerValue = String.format("attachment; filename=\"%s\"", obj.getString("filename"));
				        response.setHeader(headerKey, headerValue);
				        
				        OutputStream out=response.getOutputStream();
				        FileInputStream inStream = new FileInputStream(fileForDownload);
				        
				        byte[] buffer=new byte[4096];
				        int read=-1;
				        
				        while((read=inStream.read(buffer))!=-1) {
				        	out.write(buffer, 0, read);
				        }
				        
				        inStream.close();
				        out.close();
				        
				        //add history record
				        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
				        HistoryRecordDAO.insert(new HistoryRecord(0, timestamp, userBean.getUser().getUsername(), "download" , obj.getString("filename")));
					}else {
						response.setStatus(404);
					}
				}else if("delete".equals(action) && userBean.getUser().getPermissions().contains("d") && obj.has("parent") && obj.has("filename")) {
					if(!"A".equals(userBean.getUser().getRole())) {
						String parent=obj.getString("parent");
						File fileForDelete=null;
						String rootDir=userBean.getUser().getRootDir()+"/";
						String relativePath=parent+ obj.getString("filename");
						if(parent.startsWith(rootDir) && !relativePath.contains("..")) {
							fileForDelete=FileSystem.getFile(relativePath);
							if(fileForDelete==null) {
								System.out.println("File for delete is null");
								response.setStatus(404);
								return;
							}
							
							if(fileForDelete.exists() && fileForDelete.isFile()) {
								if(!fileForDelete.delete()) {
									response.setStatus(404);
								}else {
									//upis u history record
							        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
							        HistoryRecordDAO.insert(new HistoryRecord(0, timestamp, userBean.getUser().getUsername(), "delete" , obj.getString("filename")));
								
							        MailSender.sendMail(userBean.getUser().getUsername()+ " deleted file "+ relativePath + " at "+timestamp);
								}
							//directory missing
							}else {
								System.out.println("directory missing ");
								response.setStatus(404);
							}
						//not in root dir
						}else {
							System.out.println("not in root dir");
							response.setStatus(404);
						}
					//admin can't delete
					}else {
						System.out.println("Admin can't delete");
						response.setStatus(404);
					}
				}
				//permission missing
				else {
					System.out.println("Nedostaje permisija");
					response.setStatus(404);
				}
			}
		}else if("history".equals(action) && "A".equals(userBean.getUser().getRole())) {
			ArrayList<HistoryRecord> records=HistoryRecordDAO.getAll();
			Gson gson=new Gson();
			String json=gson.toJson(records);
			
			response.setContentType("application/json");
			PrintWriter out=response.getWriter();
			out.print(json);
			out.flush();
			out.close();
		}
		else {
			address="/WEB-INF/pages/error.jsp";
			RequestDispatcher dispatcher=request.getRequestDispatcher(address);
			dispatcher.forward(request, response);
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
