/**
 * 
 */

var currFile;

function init(){
	var container = document.getElementById("container");
	container.addEventListener("click", function(event){
	    var elem = event.target;
	    if(elem.tagName.toLowerCase() == "span" && elem !== event.currentTarget)
	    {
	        var type = elem.classList.contains("folder") ? "folder" : "file";
	        if(type=="file")
	        {
				currFile={
					filename:elem.dataset.filename,
					parent:elem.dataset.parent
				}
	        }
	        if(type=="folder")
	        {	
	            var isexpanded = elem.dataset.isexpanded=="true";
	            if(isexpanded)
	            {
	                elem.classList.remove("fa-folder-o");
	                elem.classList.add("fa-folder");
	            }
	            else
	            {
	                elem.classList.remove("fa-folder");
	                elem.classList.add("fa-folder-o");
	            }
	            elem.dataset.isexpanded = !isexpanded;
	
	            var toggleelems = [].slice.call(elem.parentElement.children);
	            var classnames = "file,folder_container".split(",");
	
	            toggleelems.forEach(function(element){
	                if(classnames.some(function(val){return element.classList.contains(val);}))
	                element.style.display = isexpanded ? "none":"block";
	            });
	        }
	    }
	});
	getContent();
}

function refresh(){
	var container = document.getElementById("container");
	container.innerHTML="";
	getContent();
}

function refreshHistory(){
	 var request = new XMLHttpRequest();
	 request.onreadystatechange = function () {
	 	if (request.readyState == 4 && request.status == 200) {
			var records=JSON.parse(request.responseText);

			var tBody=document.getElementById("tableBody");
			tBody.innerHTML="";
			
			for(let i in records){
				var tr = document.createElement('tr');
       
                var td1 = document.createElement('td');
                td1.innerText = records[i].dateTime;
                
                var td2 = document.createElement('td');
                td2.innerText = records[i].username;
                
                var td3 = document.createElement('td');
                td3.innerText = records[i].action;
                
                var td4 = document.createElement('td');
                td4.innerText=records[i].filename;
               
                tr.appendChild(td1);
                tr.appendChild(td2);
                tr.appendChild(td3);
                tr.appendChild(td4);
                tBody.appendChild(tr);
			}
				
	    }else if((request.readyState==4) && (request.status==401)){
	        document.location.href="?action=logout";
	    }
	            
	  };
	  request.open("POST", "?action=history", true);
	  request.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
	  request.send(null);

}

function getContent() {
  	var request = new XMLHttpRequest();
  	request.onreadystatechange = function () {
    if (request.readyState == 4 && request.status == 200) {
 
	var fileBean=JSON.parse(request.responseText);
	var parent=document.getElementById("container");
	parent.appendChild(dfs(fileBean,parent));

    }else if((request.readyState==4) && (request.status==401)){
        document.location.href="?action=logout";
    }
            
  };
  request.open("POST", "?action=list", true);
  request.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
  request.send(null);

}

function dfs(fileBean, parent){
	if(fileBean.isDir==true){
		var currContainer=document.createElement('div');
		currContainer.classList.add("folder_container");
		
		var currFile=document.createElement('span');
		currFile.classList.add("folder");
		
		var icon=document.createElement("i");
		icon.classList.add("material-icons");
		icon.innerText="folder";
		currFile.appendChild(icon);
		
		currFile.appendChild( document.createTextNode(fileBean.filename));
		
		currFile.setAttribute("data-parent", fileBean.parent);
		currFile.setAttribute("data-filename", fileBean.filename);
		currContainer.appendChild(currFile);
		
		currFile.setAttribute("data-isexpanded","true");
		
		for(var i in fileBean.children){
			currContainer.appendChild(dfs(fileBean.children[i], currContainer));
		}
		
		return currContainer;
	}else{
		var currFile=document.createElement('span');
		currFile.classList.add("file");
		currFile.setAttribute("data-parent", fileBean.parent);
		currFile.setAttribute("data-filename", fileBean.filename);
		
		var icon=document.createElement("i");
		icon.classList.add("material-icons");
		icon.innerText="description";
		currFile.appendChild(icon);
		
		currFile.appendChild( document.createTextNode(fileBean.filename));
		
		return currFile;
	}
}


function download(){
	if(typeof currFile == undefined || currFile==null){
		showToast("Please select a file");
		return;
	}
	
	var confirmans=confirm("Download "+ currFile.filename + "?");
	if(confirmans==false)return;
	
	var request=new XMLHttpRequest();
	request.responseType = 'blob';
	request.onreadystatechange=function(){
		if((request.readyState==4) && (request.status==200)){
			var data=request.response;
			var fileName=currFile.filename;
			save(fileName, data);
			showToast("File is downloaded");
			currFile=null;
		}else if((request.readyState==4) && (request.status==401)){
                document.location.href="?action=logout";
          }
            else if((request.readyState==4)){
			showToast("Error while downloading");
			currFile=null;
		}
	};
	request.open("POST", "?action=download", true);
	request.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
	request.send(JSON.stringify(currFile));
}

function save(filename, data) {
    const blob = new Blob([data]);
   if (navigator.msSaveBlob) {
    navigator.msSaveBlob(blob, filename);
  } else {
    const link = document.createElement('a');
    if (link.download !== undefined) {
      const url = URL.createObjectURL(blob);
      link.setAttribute('href', url);
      link.setAttribute('download', filename);
      link.style.visibility = 'hidden';
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
    }
  }
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
/*	var auth2 = gapi.auth2.getAuthInstance();
    auth2.signOut().then(function () {
      console.log('User signed out.');
    });*/
    
	var link=document.createElement("a");
	link.setAttribute("href", "?action=logout");
	document.body.appendChild(link);
	link.click();
}
