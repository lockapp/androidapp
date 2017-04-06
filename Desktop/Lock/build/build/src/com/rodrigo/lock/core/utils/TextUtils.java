package com.rodrigo.lock.core.utils;


public class TextUtils {

	public static boolean isEmpty(CharSequence str) {
		if (str == null || str.length() == 0)
			return true;
		else
			return false;
	}
	
	public static String createString (String base, String param1) {
		return base.replace("%1$s", param1);		
	}


}
