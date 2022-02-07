package org.unibl.etf.servlets;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.util.stream.Collectors;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;
import org.unibl.etf.model.beans.UserBean;
import org.unibl.etf.model.dao.HistoryRecordDAO;
import org.unibl.etf.model.dao.UserDAO;
import org.unibl.etf.model.dto.HistoryRecord;
import org.unibl.etf.util.Configuration;
import org.unibl.etf.util.FileSystem;
import org.unibl.etf.util.MailSender;
import org.unibl.etf.util.Security;

/**
 * Servlet implementation class AdditionalActionsServlet
 */
@WebServlet("/AdditionalActionsServlet")
public class AdditionalActionsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AdditionalActionsServlet() {
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
		}else if("AD".equals(userBean.getUser().getRole())) {
			String jsonBody=new BufferedReader(new InputStreamReader(request.getInputStream())).lines().collect(Collectors.joining("\n"));
			if(jsonBody==null || jsonBody.trim().length()==0) {
				response.setStatus(404);
				return;
			}
			JSONObject obj=new JSONObject(jsonBody);
			
			if("createDir".equals(action) && userBean.getUser().getPermissions().contains("c") && obj.has("parentDir") && obj.has("newDir")) {
				String parent=obj.getString("parentDir");
				String rootDir=userBean.getUser().getRootDir()+"/";
				
				if(parent.startsWith(rootDir)) {
					String newDirRelativePath=parent+obj.getString("newDir");
					
					if(newDirRelativePath.contains("..")) {
						response.setStatus(404);
						return;
					}
					
					File file=new File(Configuration.rootDir+newDirRelativePath);
					
					if(file.exists()  || !file.mkdir()) {
						response.setStatus(404);
						return;
					}
					
					Timestamp timestamp=new Timestamp(System.currentTimeMillis());
					HistoryRecordDAO.insert(new HistoryRecord(0, timestamp, userBean.getUser().getUsername(), "createDir", obj.getString("newDir")));
					
				}else {
					//not inside root
					response.setStatus(404);
					return;
				}
			}else if("deleteDir".equals(action) && userBean.getUser().getPermissions().contains("d") && obj.has("parentDir") && obj.has("dirName")) {
				String parent=obj.getString("parentDir");
				String rootDir=userBean.getUser().getRootDir()+"/";
				
				//can't delete his root
				if("".equals(parent) && obj.getString("dirName").equals(userBean.getUser().getRootDir())) {
					response.setStatus(404);
					return;
				}
				
				if(parent.startsWith(rootDir)) {
					String relativePath=parent+obj.getString("dirName");
				
					//check if it is someone's root
					Boolean isSomeones=UserDAO.getByRootDir(relativePath);
					if(isSomeones!=null && isSomeones){
						//can't delete someone's root
						response.setStatus(404);
						return;
					}
					
					File file=FileSystem.getDir(relativePath);				
					if(file==null || !FileSystem.deleteDirectory(file)) {
						//delete failed
						response.setStatus(404);
						return;
					}
					
					Timestamp timestamp=new Timestamp(System.currentTimeMillis());
					HistoryRecordDAO.insert(new HistoryRecord(0, timestamp, userBean.getUser().getUsername(), "deleteDir", obj.getString("dirName")));
					
					MailSender.sendMail(userBean.getUser().getUsername()+ " deleted directory "+ relativePath + " at "+timestamp);
				}else {
					//not inside root
					response.setStatus(404);
					return;
				}
			}else if("move".equals(action) && userBean.getUser().getPermissions().contains("u") && obj.has("file") && obj.has("srcDir") && obj.has("destDir")) {
				String filename=obj.getString("file");
				String srcDir=obj.getString("srcDir");
				String destDir=obj.getString("destDir");
				
				File srcFile=new File(Configuration.rootDir+srcDir+filename);
				File dest=new File(Configuration.rootDir+destDir);
								
				String rootDir=userBean.getUser().getRootDir()+"/";
				
				if(!srcDir.startsWith(rootDir) || !(destDir+"/").startsWith(rootDir) || destDir.contains("..") || srcDir.contains("..")) {
					//not in root
					response.setStatus(404);
					return;
				}
				
				if(srcFile.exists() && srcFile.isFile() && dest.exists() && dest.isDirectory() ) {
					File newFile=new File(Configuration.rootDir+ destDir+File.separator+ filename);
					Files.move(srcFile.toPath(), newFile.toPath());
					
					Timestamp timestamp=new Timestamp(System.currentTimeMillis());
					HistoryRecordDAO.insert(new HistoryRecord(0, timestamp, userBean.getUser().getUsername(), "move",filename));
				}else {
					//params not valid
					response.setStatus(404);
					return;
				}
				
			}else {
				//permissions missing
				response.setStatus(404);
				return;
			}
		}else {
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
