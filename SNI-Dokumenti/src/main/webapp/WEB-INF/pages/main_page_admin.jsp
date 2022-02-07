<%@page import="java.util.ArrayList"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="org.unibl.etf.model.dto.HistoryRecord"%>
<%@page import="org.owasp.encoder.Encode" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@page import="org.owasp.encoder.Encode" %>
<jsp:useBean id="userBean" type="org.unibl.etf.model.beans.UserBean" scope="session"/>
<jsp:useBean id="recordsBean" type="org.unibl.etf.model.beans.HistoryRecordBean" scope="session" />
<!DOCTYPE html>
<html>
<head>
        <title>Dokumenti</title>
        <script src="scripts/script_adm.js"></script>
        <link rel="stylesheet" href="css/style.css"/>
         <link rel="stylesheet" href="css/table.css"/>
        <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
        <meta charset="utf-8"/>
        <link rel="stylesheet"
            href="https://fonts.googleapis.com/icon?family=Material+Icons">
        <link rel="stylesheet"
            href="https://code.getmdl.io/1.3.0/material.indigo-pink.min.css">
        <script defer src="https://code.getmdl.io/1.3.0/material.min.js"></script>
        <meta name="viewport" content="width=device-width, initial-scale=1.0" />
</head>
<body onload="init()">
  <div class="mdl-layout mdl-js-layout mdl-layout--fixed-header">
        <header class="mdl-layout__header">
             <div class="mdl-layout__header-row">
                <span class="mdl-layout-title">Dokumenti</span>
                <div class="mdl-layout-spacer"></div>
                <nav class="mdl-navigation">
                     <a class="mdl-navigation__link" href="javascript:logout()">Logout</a>
                  </nav>
            </div>
        </header>
        
         <div class="mdl-tabs mdl-js-tabs mdl-js-ripple-effect">
         	<div class="mdl-tabs__tab-bar">
                <a href="#repository-panel" class="mdl-tabs__tab is-active">Repository</a> 
                <a href="#records-panel" class="mdl-tabs__tab">Records</a>
            </div>
         
             <div class="mdl-tabs__panel is-active" id="repository-panel">
            	 <div id="flex-container">
        			<div class="flex-element"  id="left-element">
        				<div id="container">
        					
        				
						</div>  
        			</div>
        			
        			<div class="flex-element"  id="right-element">
        				<button class="mdl-button mdl-js-button mdl-button--raised mdl-button--colored"  type="button" onclick="refresh()">
                                Refresh
                  		</button>
        			
        			
        				 <button class="mdl-button mdl-js-button mdl-button--raised mdl-button--colored"  type="button" onclick="download()">
                            Download
                        </button>
                        
        			</div>
                </div>
    
             </div>
             
             
             <div class="mdl-tabs__panel" id="records-panel">
         	
            	<div class="myDiv">
            		<button type="button" onclick="refreshHistory()">
                           <i class="material-icons">refresh</i>
              		</button>
            	
	            	<table id="tabela">
	            		<thead>
		            		<tr>
		            			<th>Date and time</th>
		            			<th>Username</th>
		            			<th>Action</th>
		            			<th>File name</th>
		            		</tr>
		            		
	            			
	            		</thead>
	            		<tbody id="tableBody">
							<%
							SimpleDateFormat sdf=new SimpleDateFormat("MMM d, yyyy h:mm:ss aa");
							
							for(HistoryRecord hr : recordsBean.getAll()) {%>
							<tr> 
								<td><%=Encode.forHtmlContent(sdf.format(hr.getDateTime())) %></td>
								<td><%=Encode.forHtmlContent(hr.getUsername()) %></td>
								<td><%=Encode.forHtmlContent(hr.getAction()) %></td>
								<td><%=Encode.forHtmlContent(hr.getFilename()) %></td>
							</tr>

							<%} %>	            			
	            			
	            		</tbody>
	          
	            	</table>
            	
            	</div>
            </div>
        </div>
  	</div>
  	<div aria-live="assertive" aria-atomic="true" aria-relevant="text" class="mdl-snackbar mdl-js-snackbar">
	    <div class="mdl-snackbar__text"></div>
	    <button type="button" class="mdl-snackbar__action"></button>
	</div>
</body>
</html>