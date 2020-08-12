package com.rodrigo.lock.core;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class GeneralUtils {

	private static String getPathToSave(List<File> archivosIn){
		return archivosIn.get(0).getParent();
	}
	
	private static String getEncryptFileName(){
		String name = "lockFile";//inF.getName().replace(".", "");
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hhmm ");
        String actualDate = df.format(c.getTime());
        name= actualDate + name; 
        return name;
	}
	
}
