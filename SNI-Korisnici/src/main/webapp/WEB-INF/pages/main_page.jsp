<%@page import="org.owasp.encoder.Encode"%>
<%@page import="java.util.ArrayList"%>
<%@page import="org.unibl.etf.model.dto.User"%>
<%@page import="org.owasp.encoder.Encode" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html> 
<html>
    <head>
        <title>Korisnici</title>
        <meta charset="utf-8"/>
        <link rel="stylesheet"
            href="https://fonts.googleapis.com/icon?family=Material+Icons">
        <link rel="stylesheet"
            href="https://code.getmdl.io/1.3.0/material.indigo-pink.min.css">
        <script defer src="https://code.getmdl.io/1.3.0/material.min.js"></script>
        <link rel="stylesheet" href="css/style.css">
        <script src="scripts/skripta.js"></script>
        <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
   </head>
    <body>
     <div class="mdl-layout mdl-js-layout mdl-layout--fixed-header">
        <header class="mdl-layout__header">
             <div class="mdl-layout__header-row">
                <span class="mdl-layout-title">Korisnici</span>
                <div class="mdl-layout-spacer"></div>
                <nav class="mdl-navigation">
                    <a class="mdl-navigation__link" href="?action=logout">Logout</a>
                  </nav>
            </div>
        </header>
 
        <div class="mdl-tabs mdl-js-tabs mdl-js-ripple-effect">
            <div class="mdl-tabs__tab-bar">
                <a href="#all-users-panel" class="mdl-tabs__tab is-active">All users</a> 
                <a href="#new-user-panel" class="mdl-tabs__tab">Add user</a>
            </div>
    
            <div class="mdl-tabs__panel is-active" id="all-users-panel">
               <div class="myDiv">
                    <table id="tabela">
                        <thead>
                        <tr>
                            <th>Username</th>
                            <th>Root </th>
                            <th>Role</th>
                            <th>Permissions</th>
                            <th>Ip address</th>
                            <th>Update</th>
                            <th>Delete</th>
                        </tr>
                        </thead>
                        <tbody id="tbody">
                        <% 
                    		ArrayList<User> users=(ArrayList<User>)session.getAttribute("users");
                        	int counter=0;
                    		for(User u: users){ %>
                    		<tr>
								<td><%=Encode.forHtmlContent(u.getUsername())%></td>
								<td><%=Encode.forHtmlContent(u.getRootDir())%></td>
								<td><%=Encode.forHtmlContent(u.getRole())%></td>
								<td><%=Encode.forHtmlContent(u.getPermissions()) %></td>
								<td><%=u.getIpAddress()==null?"N/A":Encode.forHtmlContent(u.getIpAddress()) %></td>
								<td><button onclick="editUser(this)" class="table_button">
                                    <i class="material-icons">edit</i>
                                </button></td>
                                <td><button onclick="deleteUser(this)" class="table_button">
                                    <i class="material-icons">delete</i>
                                </button></td>
                               <script>addUserInArray(
                            		   '<%=Encode.forJavaScript(u.getUsername()) %>',    '<%=Encode.forJavaScript(u.getRootDir()) %>', '<%=Encode.forJavaScript(u.getRole())%>', 
                            		   '<%=Encode.forJavaScript(u.getPermissions()) %>', '<%=u.getIpAddress()==null?"N/A":Encode.forJavaScript(u.getIpAddress()) %>'
                             )</script>
							</tr>
                   <% }
                    	%>
                        </tbody>
                    </table>
                    <div class="demo-card-wide mdl-card mdl-shadow--2dp dl-cell mdl-cell--12-col" id="updateForm">
                <div class="mdl-card__title">
                  <h2 class="mdl-card__title-text">Update user</h2>
                </div>
                <div class="mdl-card__supporting-text">
                    <form id="update-user-form" method="post">
                        <div class="mdl-textfield mdl-js-textfield mdl-textfield--floating-label">
                            <input class="mdl-textfield__input" type="text" id="usernameUpdate" name="username" required="required" autocomplete="off"/>
                            <label class="mdl-textfield__label" for="username">Username</label>
                        </div>
                        <div class="mdl-textfield mdl-js-textfield mdl-textfield--floating-label">
                            <input class="mdl-textfield__input" type="password" id="passwordUpdate"  name="password" autocomplete="off"/>
                            <label class="mdl-textfield__label" for="password">Password</label>
                        </div>
                        <div class="mdl-textfield mdl-js-textfield mdl-textfield--floating-label">
                                <input class="mdl-textfield__input" type="text" id="mailUpdate"  name="mail"  autocomplete="off"/>
                                <label class="mdl-textfield__label" for="mail">E-mail</label>
                         </div>

                        <div class="mdl-textfield mdl-js-textfield mdl-textfield--floating-label">
                            <select class="mdl-textfield__input" id="roleUpdate" name="role" required="required" autocomplete="off" onchange="manipulateInputForUpdate(value)">
                                <option value="A">System admin</option>
                                <option value="K">Client</option>
                                <option value="AD">Admin of documents</option>
                            </select>
                            <label class="mdl-textfield__label" for="role">Role</label>
                        </div>

                        <div id="clientAndADAdditionalUpdate">
                            <div class="mdl-textfield mdl-js-textfield mdl-textfield--floating-label">
                                <input class="mdl-textfield__input" type="text" id="rootDirUpdate"  name="rootDir" autocomplete="off"/>
                                <label class="mdl-textfield__label" for="rootDir">Root dir</label>
                            </div>
                        </div>
                        
                        <div id="clientAdditionalUpdate">
                            <div class="mdl-textfield mdl-js-textfield mdl-textfield--floating-label">
                                <input class="mdl-textfield__input" type="text" id="ipAddressUpdate"  name="ipAddress" autocomplete="off" pattern="[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}"/>
                                <label class="mdl-textfield__label" for="ipAddress">Ip address</label>
                            </div>
                          
                             <label class="mdl-checkbox mdl-js-checkbox mdl-js-ripple-effect" for="createUpdate">
                                <input type="checkbox" class="mdl-checkbox__input" id="createUpdate" name="create">
                                <span class="mdl-checkbox__label">Create</span>
                              </label>
                              <label class="mdl-checkbox mdl-js-checkbox mdl-js-ripple-effect" for="readUpdate">
                                <input type="checkbox" class="mdl-checkbox__input" id="readUpdate" name="read">
                                <span class="mdl-checkbox__label">Read</span>
                              </label>
                              
                              <label class="mdl-checkbox mdl-js-checkbox mdl-js-ripple-effect" for="updateUpdate">
                                <input type="checkbox" class="mdl-checkbox__input" id="updateUpdate" name="update" >
                                <span class="mdl-checkbox__label">Update</span>
                              </label>
                                                              
                              <label class="mdl-checkbox mdl-js-checkbox mdl-js-ripple-effect" for="deleteUpdate">
                                <input type="checkbox" class="mdl-checkbox__input" id="deleteUpdate" name="delete">
                                <span class="mdl-checkbox__label">Delete</span>
                              </label>
                        </div>
                        <div class="mdl-card__actions mdl-card--border">
                            <button class="mdl-button mdl-js-button mdl-button--raised mdl-button--colored"  type="button" onclick="updateUser()">
                                Update
                            </button>
                        </div>
                    </form>
                </div>
                <div class="mdl-card__menu">
                  <button class="mdl-button mdl-button--icon mdl-js-button mdl-js-ripple-effect"  type="button" onclick="cancel()">
                    <i class="material-icons">cancel</i>
                  </button>
                </div>
              </div>
               </div>
            
            </div>


            <div class="mdl-tabs__panel" id="new-user-panel">
                <div class="card-container mdl-card mdl-shadow--2dp">
                    <div class="mdl-card__title mdl-card--expand">
                        <h2 class="mdl-card__title-text">Add user</h2>
                    </div>
                    <div class="mdl-card__supporting-text">
                        <form id="new-user-form">
                            <div class="mdl-textfield mdl-js-textfield mdl-textfield--floating-label">
                                <input class="mdl-textfield__input" type="text" id="username" name="username" required="required" autocomplete="off"/>
                                <label class="mdl-textfield__label" for="username">Username</label>
                            </div>
                            <div class="mdl-textfield mdl-js-textfield mdl-textfield--floating-label">
                                <input class="mdl-textfield__input" type="password" id="password"  name="password" required="required" autocomplete="off" minlength="4"/>
                                <label class="mdl-textfield__label" for="password">Password</label>
                            </div>
                            
                            <div class="mdl-textfield mdl-js-textfield mdl-textfield--floating-label">
                                <input class="mdl-textfield__input" type="text" id="mail"  name="mail" required="required" autocomplete="off"/>
                                <label class="mdl-textfield__label" for="mail">E-mail</label>
                            </div>
                            
                             
                            <div class="mdl-textfield mdl-js-textfield mdl-textfield--floating-label">
                                <select class="mdl-textfield__input" id="role" name="role" required="required" autocomplete="off" onchange="manipulateInput(value)">
                                    <option value="A">System admin</option>
                                    <option value="K">Client</option>
                                    <option value="AD">Admin of documents</option>
                                </select>
                                <label class="mdl-textfield__label" for="role">Role</label>
                            </div>
                            
                            <div id="clientAndADAdditional">
                                <div class="mdl-textfield mdl-js-textfield mdl-textfield--floating-label">
                                    <input class="mdl-textfield__input" type="text" id="rootDir"  name="rootDir" autocomplete="off"/>
                                    <label class="mdl-textfield__label" for="rootDir">Root dir</label>
                                </div>
                            </div>
                            
                            <div id="clientAdditional">
                                <div class="mdl-textfield mdl-js-textfield mdl-textfield--floating-label">
                                    <input class="mdl-textfield__input" type="text" id="ipAddress"  name="ipAddress" autocomplete="off" pattern="[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}"/>
                                    <label class="mdl-textfield__label" for="ipAddress">Ip address</label>
                                </div>
                              
                              <label class="mdl-checkbox mdl-js-checkbox mdl-js-ripple-effect" for="checkbox-1">
								  <input type="checkbox" id="checkbox-1" class="mdl-checkbox__input" name="create" value="true">
								  <span class="mdl-checkbox__label">Create</span>
								</label>
								<label class="mdl-checkbox mdl-js-checkbox mdl-js-ripple-effect" for="checkbox-2">
								  <input type="checkbox" id="checkbox-2" class="mdl-checkbox__input" name="read" value="true">
								  <span class="mdl-checkbox__label">Read</span>
								</label>
								
								<label class="mdl-checkbox mdl-js-checkbox mdl-js-ripple-effect" for="checkbox-3">
								  <input type="checkbox" id="checkbox-3" class="mdl-checkbox__input" name="update" value="true">
								  <span class="mdl-checkbox__label">Update</span>
								</label>
																
								<label class="mdl-checkbox mdl-js-checkbox mdl-js-ripple-effect" for="checkbox-4">
								  <input type="checkbox" id="checkbox-4" class="mdl-checkbox__input" name="delete" value="true">
								  <span class="mdl-checkbox__label">Delete</span>
								</label>
								

                            </div>
                            <div class="mdl-card__actions mdl-card--border">
                                <button class="mdl-button mdl-js-button mdl-button--raised mdl-button--colored" type="button" onclick="add()">
                                    Add
                                </button>
                            </div>
                        </form>
                    </div>
                  
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