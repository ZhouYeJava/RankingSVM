package tool;

import java.io.*;

public class Sys {

	public static boolean deleteFile(String sPath) {  
	    boolean flag = false;  
	    File file = new File(sPath);  
	    if (file.isFile() && file.exists()) {  
	        file.delete();  
	        flag = true;  
	    }  
	    return flag;  
	}
	
	public static boolean deleteDirectory(String sPath) {  
	    if (!sPath.endsWith(File.separator)) {  
	        sPath = sPath + File.separator;  
	    }  
	    File dirFile = new File(sPath);  
	    if (!dirFile.exists() || !dirFile.isDirectory()) {  
	        return false;  
	    }  
	    boolean flag = false;  
	    File[] files = dirFile.listFiles();
	    if (files.length==0) {
	    	flag = true;
	    }
	    else {
	    	for (int i = 0; i < files.length; i++) {   
		        if (files[i].isFile()) {  
		            flag = deleteFile(files[i].getAbsolutePath());
		            if (!flag) return flag;
		        } 
		        else {  
		            flag = deleteDirectory(files[i].getAbsolutePath());   
		            if (!flag) return flag;
		        }  
		    }  
	    }
	    return flag;
	}
	
}
