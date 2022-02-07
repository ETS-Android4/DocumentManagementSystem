package org.unibl.etf.model.beans;

import java.util.ArrayList;

public class FileBean {

	private String filename;
	private boolean isDir;
	private String parent;
	private int level;
	private ArrayList<FileBean> children;

	public FileBean() {
		// TODO Auto-generated constructor stub
	}

	public FileBean(String filename, boolean isDir, String parent, int level) {
		super();
		this.filename = filename;
		this.isDir = isDir;
		this.parent = parent;
		this.level = level;
		if(isDir) {
			children=new ArrayList<FileBean>();
		}
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public boolean isDir() {
		return isDir;
	}

	public void setDir(boolean isDir) {
		this.isDir = isDir;
	}

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public ArrayList<FileBean> getChildren() {
		return children;
	}

	public void setChildren(ArrayList<FileBean> children) {
		this.children = children;
	}
	
}
