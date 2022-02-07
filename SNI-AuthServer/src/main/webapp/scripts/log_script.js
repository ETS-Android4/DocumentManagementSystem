/**
 * 
 */

function onSignIn(googleUser){	
	var form=document.createElement('form');
	form.setAttribute("action", "?action=auth");
	form.setAttribute("method", "POST");
	
	var input=document.createElement('input');
	input.setAttribute("type", "hidden");
	input.setAttribute("name","id_token");
	input.setAttribute("value", googleUser.getAuthResponse().id_token);
	
	form.appendChild(input);
	var body=document.getElementsByTagName("body")[0];
	body.appendChild(form);
	form.submit();
	
	googleUser.disconnect()
	
/*	var object={
		id_token: googleUser.getAuthResponse().id_token
	}*/

/*	var request=new XMLHttpRequest();
	request.onreadystatechange=function(){
		if((request.readyState==4) && (request.status==200)){
			var link=document.createElement("a");
			link.setAttribute("href", "?action=authenticated");
			document.body.appendChild(link);
      		link.click();
		}else if(request.readyState==4){
			console.log(request.status);
			alert("error");
		}
	};
	request.open("POST", "?action=auth", true);
	request.send(JSON.stringify(object));	*/
}