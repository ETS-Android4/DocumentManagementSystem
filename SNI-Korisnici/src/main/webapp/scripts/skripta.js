/**
 * 
 */
 var users=[];
 var currEditIndex=0;
 
 function deleteUser(){
    var tableRef = document.getElementById("tabela");
    var tbody = tableRef.querySelector("tbody");

    var checkedInputs = document.querySelectorAll("input[type='checkbox']:checked");
    Array.prototype.slice.call(checkedInputs)
    .forEach( input => {
        tbody.removeChild(input.parentNode);
    })
}

function addUserInArray(u, rd, r,  p, ia){
	var object={
		username:u,
		rootDir:rd,
		role:r,
		permissions:p,
		ipAddress:ia
	}

	users.push(object);
}

function manipulateInput(value){
    var clientAdditional=document.getElementById("clientAdditional");
    var clientAndADAdditional=document.getElementById("clientAndADAdditional");
    if(value=='K'){
        clientAdditional.style.display="block";
    }else{
        clientAdditional.style.display="none";
    }
    if(value!='A'){
        clientAndADAdditional.style.display="block";
    }else{
        clientAndADAdditional.style.display="none";
    }
}
  function deleteUser(button){
	
    if(confirm("Are you sure?")==true){
    	var row=button.parentNode.parentNode;
    	var index=row.rowIndex-1;
    	
    	console.log(index);
    	var realUsername=users[index].username;
    
        var request=new XMLHttpRequest();
        request.onreadystatechange=function(){
            if((request.readyState==4) && (request.status==200)){
                let tableRef = document.getElementById('tabela');
                let tbody = tableRef.querySelector("tbody");
                tbody.removeChild(button.parentNode.parentNode);
                users.splice(index, 1);
                showToast("User deleted!");
                
            }else if((request.readyState==4) && (request.status==401)){
                document.location.href="?action=logout";
            }
            
            else if(request.readyState==4){
                showToast("User can't be deleted!");
            }
        }

        var object={
            username:realUsername
        }
        request.open("POST", "?action=delete", true);
        request.send(JSON.stringify(object));
    }         
}
            
function add(){
		var elements=document.forms["new-user-form"].elements;
		if(elements.role.value!="A"){
			if(elements.rootDir.value==null || elements.rootDir.value==""){
				showToast("All fields must be filled");
				return;
			}
		}
		if(elements.role.value=="K"){
			var re = new RegExp("[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}");
			if(elements.ipAddress.value==null || elements.ipAddress.value=="" || !re.test(elements.ipAddress.value)){
				showToast("All fields must be filled");
				return;
			}
		}
		
		if(!document.forms["new-user-form"].checkValidity()){
			showToast("All fields must be filled");
			return;
		}

        var elements=document.forms["new-user-form"].elements;
 
        var object={
            username:elements.username.value,
            password:elements.password.value,
            mail:elements.mail.value,
            role: elements.role.value,
            rootDir:elements.rootDir.value,
            ipAddress:elements.ipAddress.value,
            create:(elements.create.checked?"true":"false"),
            read:(elements.read.checked?"true":"false"),
            update:(elements.update.checked?"true":"false"),
            delete:(elements.delete.checked?"true":"false"),
        }
		
		
        var request=new XMLHttpRequest();
        request.onreadystatechange=function(){
            if((request.readyState==4) && (request.status==200)){
                var body=document.getElementById("tbody");

                var tr = document.createElement('tr');
       
                var td1 = document.createElement('td');
                td1.innerText = object.username;
                
                var rootDir;
                if(object.role=="A"){
                	rootDir="root";
                }else{
                	rootDir=object.rootDir;
                }
                
                var td2 = document.createElement('td');
                td2.innerText = rootDir;
                
                var td3 = document.createElement('td');
                td3.innerText = object.role;
                var td4 = document.createElement('td');
               	var prms;
                if(object.role!="K"){
                    prms="crud";
                }else{
                    prms=(object.create=="true"?"c":"")+(object.read=="true"?"r":"")+(object.update=="true"?"u":"")+(object.delete=="true"?"d":"");
                }
                td4.innerHTML=prms;
                var td5= document.createElement('td');
                td5.innerText=(object.role=='K'?object.ipAddress:"N/A");

                var td6=document.createElement('td');
                td6.innerHTML=["<td><button onclick=\"editUser(this)\" class=\"table_button\">"," <i class=\"material-icons\">edit</i>" ," </button></td>" ].join(' ');
            
                var td7=document.createElement('td');
                td7.innerHTML=["<td><button onclick=\"deleteUser(this)\" class=\"table_button\">"," <i class=\"material-icons\">delete</i>" ," </button></td>" ].join(' ');
           
                tr.appendChild(td1);
                tr.appendChild(td2);
                tr.appendChild(td3);
                tr.appendChild(td4);
                tr.appendChild(td5);
                tr.appendChild(td6);
                tr.appendChild(td7);

                body.appendChild(tr);
                
                var user={
                	username:object.username,
                	rootDir:object.rootDir,
                	role:object.role,
                	permissions:prms,
                	ipAddress:object.ipAddress
                };
				users.push(user);
				
				document.forms["new-user-form"].reset();
				showToast("Successfully added");
			 	document.getElementById("clientAdditional").style.display="none";
			 	document.getElementById("clientAndADAdditional").style.display="none";
           
            }else if((request.readyState==4) && (request.status==401)){
                document.location.href="?action=logout";
            }
            
            
            else if(request.readyState==4){
                showToast("User can't be added!");
            }
        };
        request.open("POST", "?action=add", true);
        request.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
        request.send(JSON.stringify(object));
    }
            
            
 function cancel(){
    var updateForm=document.getElementById("updateForm");
    updateForm.style.display="none";

    var clientAdditional=document.getElementById("clientAdditionalUpdate");
    clientAdditionalUpdate.style.display="none";
    var clientAndADAdditional=document.getElementById("clientAndADAdditionalUpdate");
    clientAndADAdditionalUpdate.style.display="none";

    document.forms["update-user-form"].reset();

    document.getElementById("createUpdate").checked=false;
    document.getElementById("createUpdate").parentElement.classList.remove('is-checked');
    document.getElementById("readUpdate").checked=false;
    document.getElementById("readUpdate").parentElement.classList.remove('is-checked');
    document.getElementById("updateUpdate").checked=false;
    document.getElementById("updateUpdate").parentElement.classList.remove('is-checked');
    document.getElementById("deleteUpdate").checked=false;
    document.getElementById("deleteUpdate").parentElement.classList.remove('is-checked');
}
            
function manipulateInputForUpdate(value){
    var clientAdditional=document.getElementById("clientAdditionalUpdate");
    var clientAndADAdditional=document.getElementById("clientAndADAdditionalUpdate");
    if(value=="K"){
        clientAdditional.style.display="block";
    }else{
        clientAdditional.style.display="none";
    }
    if(value!="A"){
        clientAndADAdditional.style.display="block";
    }else{
        clientAndADAdditional.style.display="none";
    }
}

            
function editUser(button){
	//set form fields
    var tableRow=button.parentNode.parentNode;
    currEditIndex=tableRow.rowIndex-1; //zbog headera

    var updateForm=document.getElementById("updateForm");

    //vec je otvoreno
    if(updateForm.style.display=="block"){
        document.forms["update-user-form"].reset();
        document.getElementById("clientAdditionalUpdate").style.display="none";
        document.getElementById("clientAndADAdditionalUpdate").style.display="none";
    }
    updateForm.style.display="block";

   
    document.getElementById("usernameUpdate").value=users[currEditIndex].username;
    document.getElementById("usernameUpdate").parentElement.classList.add('is-focused');
    document.getElementById("usernameUpdate").parentElement.classList.add('is-upgraded');

    document.getElementById("roleUpdate").value=users[currEditIndex].role;
    document.getElementById("roleUpdate").parentElement.classList.add('is-focused');
    document.getElementById("roleUpdate").parentElement.classList.add('is-upgraded');


    var role=tableRow.cells[2].innerHTML;
    if(role=="AD" || role=="K"){
        document.getElementById("rootDirUpdate").value=users[currEditIndex].rootDir;
        document.getElementById("rootDirUpdate").parentElement.classList.add('is-focused');
        document.getElementById("rootDirUpdate").parentElement.classList.add('is-upgraded');

        document.getElementById("clientAndADAdditionalUpdate").style.display="block";
    }
    if(role=="K"){
        
        document.getElementById("ipAddressUpdate").value=users[currEditIndex].ipAddress;
        document.getElementById("ipAddressUpdate").parentElement.classList.add('is-focused');
        document.getElementById("ipAddressUpdate").parentElement.classList.add('is-upgraded');


        var permissions=users[currEditIndex].permissions;
        if(permissions.includes("c")){
            document.getElementById("createUpdate").checked=true;
            document.getElementById("createUpdate").parentElement.classList.add('is-checked');
        }
        if(permissions.includes("r")){
            document.getElementById("readUpdate").checked=true;
            document.getElementById("readUpdate").parentElement.classList.add('is-checked');
           
        }
        if(permissions.includes("u")){
            document.getElementById("updateUpdate").checked=true;
            document.getElementById("updateUpdate").parentElement.classList.add('is-checked');
        }
        if(permissions.includes("d")){
            document.getElementById("deleteUpdate").checked=true;
            document.getElementById("deleteUpdate").parentElement.classList.add('is-checked');
        }

        document.getElementById("clientAdditionalUpdate").style.display="block";
      
    }       
}

function updateUser(){
	var elements=document.forms["update-user-form"].elements;
	if(elements.roleUpdate.value!="A"){
		if(elements.rootDirUpdate.value==null || elements.rootDirUpdate.value==""){
			showToast("All fields must be filled");
			return;
		}
	}
	if(elements.roleUpdate.value=="K"){
		var re = new RegExp("[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}");
		if(elements.ipAddressUpdate.value==null || elements.ipAddressUpdate.value=="" || !re.test(elements.ipAddressUpdate.value)){
			showToast("All fields must be filled correctly");
			return;
		}
	}
	
	if(!document.forms["update-user-form"].checkValidity()){
		showToast("All fields must be filled");
		return;
	}
	
    var oldUsername=users[currEditIndex].username;

    var object={
        prevUsername:oldUsername,
        username:elements.username.value,
        password:elements.password.value,
        mail:elements.mail.value,
        role: elements.role.value,
        rootDir:elements.rootDir.value,
        ipAddress:elements.ipAddress.value,
        create:(elements.create.checked?"true":"false"),
        read:(elements.read.checked?"true":"false"),
        update:(elements.update.checked?"true":"false"),
        delete:(elements.delete.checked?"true":"false"),
    }

    var request=new XMLHttpRequest();
    request.onreadystatechange=function(){
        if((request.readyState==4) && (request.status==200)){                  	
            //update table fields
            //get row
            let tableRef = document.getElementById('tabela');
            var row=tableRef.rows[currEditIndex+1];

            row.cells[0].innerText=object.username;
           
            var root;
            if(object.role=="A"){
            	root="root";
            }else{
            	root=object.rootDir;
            }
            row.cells[1].innerText=root;

            row.cells[2].innerText=object.role;
            
            var prms;
            if(object.role!="K"){
                prms="crud";
            }else{
                prms=(object.create=="true"?"c":"")+(object.read=="true"?"r":"")+(object.update=="true"?"u":"")+(object.delete=="true"?"d":"");
            }
            row.cells[3].innerText=prms;
            row.cells[4].innerText= (object.role=='K'?object.ipAddress:"N/A");

            //update array object
            var updatedUser={
                username:object.username,
                role:object.role,
                rootDir:root,
                permissions:prms,
                ipAddress:(object.role=='K'?object.ipAddress:"N/A")
            }

            users[currEditIndex]=updatedUser;
            
            showToast("User updated");
            cancel();
        }else if((request.readyState==4) && (request.status==401)){
                document.location.href="?action=logout";
            }
        
        else if(request.readyState==4){
            showToast("User can't be updated!");
        }
    };
    request.open("POST", "?action=update", true);
    request.setRequestHeader("Content-Type", "application/json;charset=UTF-8")
    request.send(JSON.stringify(object));
}
            
function showToast(msg){
	var notification = document.querySelector('.mdl-js-snackbar');
	notification.MaterialSnackbar.showSnackbar(
	  {
	    message: msg
	  }
	);
}

function logout(){
	console.log("u logout");

	var link=document.createElement("a");
	link.setAttribute("href", "?action=logout");
	document.body.appendChild(link);
	link.click();
}

function checkSession(){
	/*var ssoValid=true;
	var request=new XMLHttpRequest();

	request.onreadystatechange=function(){
		if((request.readyState==4) && (request.status==200)){
			
		}else if(request.readyState==4){
			ssoValid=false;
		}
	}
	request.open("GET", "https://localhost:8443/SNI-AuthServer/check", false);
	request.send(null);*/
	
	return true;
}