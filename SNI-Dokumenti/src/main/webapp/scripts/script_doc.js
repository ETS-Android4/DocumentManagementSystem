/**
 * 
 */
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
				document.forms["new-dir-form"].elements.parentDir.value=destination.parent+destination.filename;
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
      var text = request.responseText;
      console.log(text);

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
		
		//set as droppable
		currFile.setAttribute("ondrop", "drop(event)");
		currFile.setAttribute("ondragover", "allowDrop(event)");
		
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
		
		//set as draggable
		currFile.setAttribute("draggable", "true");
		currFile.setAttribute("ondragstart", "drag(event)")
		
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
		//set as draggable
		currFile.setAttribute("draggable", "true");
		currFile.setAttribute("ondragstart", "drag(event)")
			
		var icon=document.createElement("i");
		icon.classList.add("material-icons");
		icon.innerText="description";
		currFile.appendChild(icon);
		
		currFile.appendChild( document.createTextNode(childName));
		
		parentNode.appendChild(currFile);	
}

function createFolder(parentNode, childName, parentSpan){
		var currContainer=document.createElement('div');
		currContainer.classList.add("folder_container");
		
		var currFile=document.createElement('span');
		currFile.classList.add("folder");
		
		var icon=document.createElement("i");
		icon.classList.add("material-icons");
		icon.innerText="folder";
		currFile.appendChild(icon);
		
		currFile.appendChild( document.createTextNode(childName));
		
		currFile.setAttribute("data-parent", parentSpan.dataset.parent+parentSpan.dataset.filename+"/");
		currFile.setAttribute("data-filename", childName);
		currFile.setAttribute("data-isexpanded","true");	
		//set as droppable
		currFile.setAttribute("ondrop", "drop(event)");
		currFile.setAttribute("ondragover", "allowDrop(event)");
		
		currContainer.appendChild(currFile);	
		parentNode.appendChild(currContainer);
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
		showToast("Please select a directory");
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
				var parent=document.querySelector('[data-filename="'+ object.dest+'"][data-parent="'+object.parent+'"]');
				var child=fileUpload.files[0].name;
				createFile(parent.parentNode, child, parent);
				document.forms["upload-file-form"].reset();
				showToast("File is uploaded");
				
			}else{
				showToast("File is updated");
				document.forms["update-file-form"].reset();	
			}
			clearDirNames();
		}else if((request.readyState==4)){
			
			if(!isUpdate){
				showToast("Error. File can't be uploaded");
				document.forms["upload-file-form"].reset();
				
			}else{
				showToast("Error. File can't be updated");
				document.forms["update-file-form"].reset();
				
			}
			clearDirNames();
			
		}
	};
	if(!isUpdate){
		request.open("POST", "?action=upload", true);
	}else{
		request.open("POST", "?action=update", true);
	}
	request.send(formData);
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

function createDir(){
	var elements=document.forms["new-dir-form"].elements;
	var newDirName=elements.dirName.value;
	var parent=elements.parentDir.value;
	
	if(typeof destination == undefined || destination==null){
		showToast("Please select a parent directory");
		return;
	}
		
	if(parent=="" || newDirName==""){
		showToast("All fields must be filled");
		return;
	}
	
	var object={
		parentDir:destination.parent+destination.filename+"/",
		newDir:newDirName
	}
	
	var request=new XMLHttpRequest();
	request.onreadystatechange=function(){
		if((request.readyState==4) && (request.status==200)){
			showToast("Directory is created");
			var parentSpan=document.querySelector('[data-filename="'+destination.filename+'"][data-parent="'+destination.parent+'"]');
			createFolder(parentSpan.parentNode, object.newDir, parentSpan);
			clearDirNames();
		}else if((request.readyState==4)){
			showToast("Error. Directory can't be created");
			clearDirNames();
		}
	}
	request.open("POST", "?action=createDir", true);
	request.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
	request.send(JSON.stringify(object));
}

function deleteDir(){
	if(typeof destination == undefined || destination==null){
		showToast("Please select a directory");
		return;
	}
	
	var confirmans=confirm("Delete "+ destination.filename + " and all its content?");
	if(confirmans==false)return;
	
	
	var object={
		parentDir:destination.parent,
		dirName:destination.filename
	}
	
	var request=new XMLHttpRequest();
	request.onreadystatechange=function(){
		if((request.readyState==4) && (request.status==200)){
			showToast("Directory is deleted");
			var currSpan=document.querySelector('[data-filename="'+destination.filename+'"][data-parent="'+destination.parent+'"]');
			//remove njgov dic folder container
			
			currSpan.parentNode.remove();
			clearDirNames();
		}else if((request.readyState==4)){
			showToast("Error. Directory can't be deleted");
			clearDirNames();
		}
	}
	request.open("POST", "?action=deleteDir", true);
	request.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
	request.send(JSON.stringify(object));
	
}

function clearDirNames(){
	document.forms["new-dir-form"].reset();
	document.getElementById("destUpdate").innerText="";
	document.getElementById("dest").innerText="";
	destination=null;
}


//drag and drop
function allowDrop(ev) {
  ev.preventDefault();
}

function drag(ev) {
  ev.dataTransfer.setData("filename", ev.target.dataset.filename);
  ev.dataTransfer.setData("parent", ev.target.dataset.parent);
}

function drop(ev) {
	if(!ev.target.classList.contains("folder")){
		return;
	}
	
  	var filename = ev.dataTransfer.getData("filename");
  	var src=ev.dataTransfer.getData("parent");

	var dest=ev.target.dataset.filename;
	var destPath=ev.target.dataset.parent;
	
	var confirmAns=confirm("Move " + filename+" from:"+src+ " to:"+ destPath+dest+"?");
	if(confirmAns==false){
		return;
	}
	
	var object={
		file:filename,
		srcDir:src,
		destDir:destPath+dest
	}
	
	var request=new XMLHttpRequest();
	request.onreadystatechange=function(){
		if((request.readyState==4) && (request.status==200)){
			//	function createFile(parentNode, childName, parentSpan)
			createFile(ev.target.parentNode, filename, ev.target);
			
			var fileSpan=document.querySelector('[data-filename="'+filename+'"][data-parent="'+src+'"]');
			fileSpan.remove();	
		}else if(request.readyState==4){
			showToast("Moving file is not possible");
		}
	}
	request.open("POST", "?action=move", true);
	request.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
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

