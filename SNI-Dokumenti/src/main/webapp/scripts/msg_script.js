/**
 * 
 */
 
 function logout(){
	var link=document.createElement("a");
	link.setAttribute("href", "?action=logout");
	document.body.appendChild(link);
	link.click();
}