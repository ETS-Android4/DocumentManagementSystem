/**
 * 
 */
var currFile;
var destination;

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
		
				destination={
					filename:elem.dataset.filename,
					parent:elem.dataset.parent
				}
				document.getElementById("dest").innerText=destination.filename;
				document.getElementById("destUpdate").innerText=destination.filename;

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


function getContent() {
  var request = new XMLHttpRequest();
  request.onreadystatechange = function () {
    if (request.readyState == 4 && request.status == 200) {
		var fileBean=JSON.parse(request.responseText);
		var parent=document.getElementById("container");
		parent.appendChild(dfs(fileBean,parent));

    }
  };
  request.open("POST", "?action=list", true);
  request.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
  request.send(null);
}



function populateContainer(json){
	var fileBean=JSON.parse(json);
	var parent=document.getElementById("container");
	parent.appendChild(dfs(fileBean,parent));
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

function createFile(parentNode, childName, parentSpan){
	var currFile=document.createElement('span');
		currFile.classList.add("file");
		currFile.setAttribute("data-parent", parentSpan.dataset.parent+parentSpan.dataset.filename+"/");
		currFile.setAttribute("data-filename", childName);
			
		var icon=document.createElement("i");
		icon.classList.add("material-icons");
		icon.innerText="description";
		currFile.appendChild(icon);
		
		currFile.appendChild( document.createTextNode(childName));
		
		parentNode.appendChild(currFile);
		
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
		}else if((request.readyState==4)){
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

function uploadFile(isUpdate){
	if(typeof destination == undefined || destination==null){
		showToast("Please select directory");
		return;
	}
	if(!isUpdate && document.getElementById("fileUpload").files.length<1){
		showToast("File is missing");
		return;
	}else if(isUpdate && document.getElementById("fileUpdate").files.length<1){
		showToast("File is missing");
		return;
	}
	
	let formData = new FormData();     
	var object={
		dest:destination.filename,
		parent:destination.parent,
	}
	formData.append('objArr', JSON.stringify( object ));
	
	var fileChooser;
	if(!isUpdate){
		fileChooser=document.getElementById("fileUpload");  	
	}else{
		fileChooser=document.getElementById("fileUpdate");  	
	}
	
	if(fileChooser.files[0].size>1024*1024*5){
		showToast("File too big, max size is 5MB");
		return;
	}
	formData.append("file", fileChooser.files[0]);
	
	var request=new XMLHttpRequest();
	request.onreadystatechange=function(){
		if((request.readyState==4) && (request.status==200)){
			if(!isUpdate){
				showToast("File is uploaded");
				var parent=document.querySelector('[data-filename="'+ object.dest+'"][data-parent="'+object.parent+'"]');
				var child=fileUpload.files[0].name;
				createFile(parent.parentNode, child, parent);
				document.forms["upload-file-form"].reset();
				
			}else{
				showToast("File is updated");
				document.forms["update-file-form"].reset();	
			}
			clearDest();
		}else if((request.readyState==4)){
			if(!isUpdate){
				showToast("Error. File can't be uploaded");
				document.forms["upload-file-form"].reset();
				
			}else{
				showToast("Error. File can't be updated");
				document.forms["update-file-form"].reset();
				
			}
			clearDest();
		}
	};
	if(!isUpdate){
		request.open("POST", "?action=upload", true);
	}else{
		request.open("POST", "?action=update", true);
	}
	request.send(formData);
}

function clearDest(){
	document.getElementById("destUpdate").innerText="";
	document.getElementById("dest").innerText="";
	destination=null;
}

function deleteFile(){
	if(typeof currFile == undefined || currFile==null){
		showToast("Please select a file");
		return;
	}
	
	var confirmans=confirm("Delete "+ currFile.filename + "?");
	if(confirmans==false)return;
	
	var request=new XMLHttpRequest();

	request.onreadystatechange=function(){
		if((request.readyState==4) && (request.status==200)){
			var currentNode=document.querySelector('[data-filename="'+ currFile.filename+'"][data-parent="'+currFile.parent+'"]');
			currentNode.remove();
			showToast("File is deleted");
			currFile=null;
		}else if((request.readyState==4)){
			showToast("Error while deleting");
			currFile=null;
		}
	};
	request.open("POST", "?action=delete", true);
	request.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
	request.send(JSON.stringify(currFile));
}


function showToast(msg){
	var notification = document.querySelector('.mdl-js-snackbar');
	notification.MaterialSnackbar.showSnackbar(
	  {
	    message: msg
	  }
	);
}
