<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
		<title>Documents</title>
 		<meta charset="utf-8"/>
        <link rel="stylesheet"
            href="https://fonts.googleapis.com/icon?family=Material+Icons">
        <link rel="stylesheet"
            href="https://code.getmdl.io/1.3.0/material.indigo-pink.min.css">
        <script defer src="https://code.getmdl.io/1.3.0/material.min.js"></script>
        <link rel="stylesheet" href="css/login_style.css">
        <script src="scripts/msg_script.js"></script>
</head>
<body>
	 <div class="card-container mdl-card mdl-shadow--2dp">
            <div class="mdl-card__title mdl-card--expand">
                <h2 class="mdl-card__title-text">Documents</h2>
            </div>
            <div class="mdl-card__supporting-text">
               <p>You are already logged in, you need to log out before logging as a different user.</p>
            </div>
            
             <div class="mdl-card__actions">
                 <button class="mdl-button mdl-js-button mdl-button--primary" type="button" onclick="logout()">
                     Log out
                   </button>
             </div>
      </div>
</body>
</html>