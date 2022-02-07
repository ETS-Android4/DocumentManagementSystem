<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@page import="org.owasp.encoder.Encode" %>
<jsp:useBean id="userBean" type="org.unibl.etf.model.beans.UserBean" scope="session"/>
<!DOCTYPE html>
<html>
<head>
        <title>Dokumenti</title>
        <script src="scripts/script_doc.js"></script>
        <link rel="stylesheet" href="css/style.css"/>
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
                    <a class="mdl-navigation__link" href="?action=logout">Logout</a>
                  </nav>
            </div>
        </header>
        <div id="flex-container">
        	<div class="flex-element"  id="left-element">
        		<div id="container">
				</div>  
        
        	</div>
        	<div class="flex-element" id="right-element">
        		
        		  <button class="mdl-button mdl-js-button mdl-button--raised mdl-button--colored"  type="button" onclick="refresh()">
                                Refresh
                  </button>
        	
        		  <button class="mdl-button mdl-js-button mdl-button--raised mdl-button--colored"  type="button" onclick="download()">
                                Download
                  </button>
                  
        	
        	
           		 <div>
                   	<form id="upload-file-form" enctype="multipart/form-data" accept-charset="utf-8">
				  	 <input type="file" id="fileUpload" name="fileUpload"> 
				 <!--    	<label for="fileUpload" class="mdl-button mdl-js-button mdl-button--colored">
					  <i class="material-icons">+</i>
					</label> -->
				<!-- 	<label for="file-upload" class="custom-file-upload">
					    <i class="material-icons">upload</i> Custom Upload
					</label>
					<input id="file-upload" type="file" id="fileUpload"/> -->
					
		            <button class="mdl-button mdl-js-button mdl-button--raised mdl-button--colored"  type="button" onclick="uploadFile(false)">
                                Upload
                 	</button>

        			</form> 
	            		<label id="dest">
						</label>
            	</div>
            	
            	  <div>
                   	<form id="update-file-form" enctype="multipart/form-data" accept-charset="utf-8">
					<input type="file" id="fileUpdate" name="fileUpdate">
				    <!--  <label class="mdl-button mdl-js-button mdl-button--icon mdl-button--file">
					  <i class="material-icons">attach_file</i>
					</label> -->
					
		            <button class="mdl-button mdl-js-button mdl-button--raised mdl-button--colored"  type="button" onclick="uploadFile(true)">
                                Update
                 	</button>

        			</form> 
            		<label id="destUpdate">
					</label>
            	</div>
                  
                  	<div>
	        		 <button class="mdl-button mdl-js-button mdl-button--raised mdl-button--colored"  type="button" onclick="deleteFile()">
	                                Delete
	                  </button>
                  
        			</div>
        			
        			
        	<div>
				<form id="new-dir-form">
				 	<label class="mdl-textfield__label" for="username">Parent directory name:</label>
		           	<input class="mdl-textfield__input" type="text" name="parentDir" id="parentDir" readonly required="required" autocomplete="off">
		          
				
					<div class="mdl-textfield mdl-js-textfield mdl-textfield--floating-label">
				           <input class="mdl-textfield__input" type="text" name="dirName" id="dirName" required="required" autocomplete="off">
				           <label class="mdl-textfield__label" for="username">Directory name:</label>
					</div>
				
					<button class="mdl-button mdl-js-button mdl-button--raised mdl-button--colored"  type="button" onclick="createDir()">
				                     Create dir
				     </button>
				</form>     
			</div>
			
			<div>
				<button class="mdl-button mdl-js-button mdl-button--raised mdl-button--colored"  type="button" onclick="deleteDir()">
		                    Delete dir
		       </button>
			</div>
        
      
        </div>
	</div>
	<div aria-live="assertive" aria-atomic="true" aria-relevant="text" class="mdl-snackbar mdl-js-snackbar">
	    <div class="mdl-snackbar__text"></div>
	    <button type="button" class="mdl-snackbar__action"></button>
	</div>
</body>
</html>

