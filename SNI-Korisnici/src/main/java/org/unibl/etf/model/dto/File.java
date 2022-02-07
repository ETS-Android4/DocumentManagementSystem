package org.unibl.etf.model.dto;

import java.io.Serializable;

public class File implements Serializable {

	private int fileId;
	private String name;
	private boolean isDir;
	private int parentId;
	
	public File() {
		// TODO Auto-generated constructor stub
	}
	
	

	public File(int fileId) {
		super();
		this.fileId = fileId;
	}
	
	public File(int fileId, String name, boolean isDir, int parentId) {
		super();
		this.fileId = fileId;
		this.name = name;
		this.isDir = isDir;
		this.parentId = parentId;
	}

	public int getFileId() {
		return fileId;
	}

	public void setFileId(int fileId) {
		this.fileId = fileId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isDir() {
		return isDir;
	}

	public void setDir(boolean isDir) {
		this.isDir = isDir;
	}

	public int getParentId() {
		return parentId;
	}

	public void setParentId(int parentId) {
		this.parentId = parentId;
	}
	
	

}
