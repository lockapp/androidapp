package com.rodrigo.lock.app.Utils;

import java.util.Locale;
import java.util.ResourceBundle;

public class LenguajeUtils {

	public static boolean isEmpty(CharSequence str) {
		if (str == null || str.length() == 0)
			return true;
		else
			return false;
	}
	
	public static String createString (String base, String param1) {
		return base.replace("%1$s", param1);		
	}
	
		
	
	public static ResourceBundle getBundle(){
		try{
			return  ResourceBundle.getBundle("language.lang");
		}catch (Exception e){
			Locale l = new Locale("en", "EN");
			return ResourceBundle.getBundle("language.lang", l);
		}
	}


}
