package org.unibl.etf.util;

import java.io.File;
import java.util.ArrayList;

import org.unibl.etf.model.beans.FileBean;

public class FileSystem {

	private static ArrayList<FileBean> result=new ArrayList<FileBean>();
	private static int counter=0;
	private static String parent="";
	
	public static FileBean traverseDirectory(String dir){
		File root=null;
		if("root".equals(dir)) {
			root=new File(Configuration.rootDir);
		}else {
			root=new File(Configuration.rootDir+dir);
		}
		if(!root.exists()) {
			return null;
		}
		
		return dfs(root, 0);
	}
	
	private static FileBean dfs(File root, int level) {
		FileBean bean=new FileBean();
		bean.setFilename(root.getName());
		bean.setLevel(level);
		bean.setParent(parent);
		if(root.isDirectory()) {
			bean.setDir(true);
		}else {
			bean.setDir(false);
		}
		
		if(root.isDirectory()) {
			bean.setChildren(new ArrayList<FileBean>());
			
			parent+=root.getName()+"/";
			
			for(File f: root.listFiles()) {
				FileBean child=dfs(f, level+1);
				bean.getChildren().add(child);
			}
			parent=parent.substring(0, parent.length()-(root.getName().length()+1));
		}
		return bean;
	}
	
	public static File getDir(String relativePath) {
		String apsolutePath=Configuration.rootDir+relativePath;
		File file=new File(apsolutePath);
		if(file.exists() && file.isDirectory()) {
			return file;
		}else {
			return null;
		}
	}
	
	
	public static File getFile(String relativePath) {
		String apsolutePath=Configuration.rootDir+relativePath;
		File file=new File(apsolutePath);
		if(file.exists() && file.isFile()) {
			return file;
		}else {
			return null;
		}
	}
	
	public static boolean deleteDirectory(File f) {
		File[] content=f.listFiles();
		if(content!=null) {
			for(File file : content) {
				deleteDirectory(file);
			}
		}
		return f.delete();
	}


}
