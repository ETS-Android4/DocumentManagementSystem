<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html> 
<html>
    <head>
        <title>Login</title>
        <meta charset="utf-8"/>
        <link rel="stylesheet"
            href="https://fonts.googleapis.com/icon?family=Material+Icons">
        <link rel="stylesheet"
            href="https://code.getmdl.io/1.3.0/material.indigo-pink.min.css">
        <script defer src="https://code.getmdl.io/1.3.0/material.min.js"></script>
        <link rel="stylesheet" href="css/login_style.css">
    </head>
    <body>
        <div class="card-container mdl-card mdl-shadow--2dp">
            <div class="mdl-card__title mdl-card--expand">
                <h2 class="mdl-card__title-text">Log in</h2>
            </div>
            <div class="mdl-card__supporting-text">
                <form id="new-student-form" method="post" action="?action=second_login_u">
                    <div class="mdl-textfield mdl-js-textfield mdl-textfield--floating-label">
                        <input class="mdl-textfield__input" type="token" name="token" id="token" required="required" autocomplete="off">
                        <label class="mdl-textfield__label" for="token">Token:</label>
                    </div>

                    <div class="mdl-card__actions">
                        <button class="mdl-button mdl-js-button mdl-button--primary" type="submit">
                           Log in
                          </button>
                    </div>
                    <div class="mdl-card--border">
                       <p></p>
                    </div>
                </form>
            </div>
        </div>
    </body>
</html>