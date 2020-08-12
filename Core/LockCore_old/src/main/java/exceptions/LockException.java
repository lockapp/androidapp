package exceptions;

import java.util.LinkedList;
import java.util.List;

//LinkedList<String> lparam = new LinkedList<>();
//lparam.add(toEncrypt);
//throw new LockException(LockException.error_open, lparam);

public class LockException extends RuntimeException{
	public final static String not_found="err_not_found";
	public final static String file_not_found_2="error_notfound2";
	public final static String error_open="error_open";
	public final static String error_lock2="error_lock2";
	public final static String error_delete2="error_delete2";	
	public final static String error_version="error_version";
	public final static String error_nosecureview="error_nosecureview";
	public final static String error_defeated="error_defeated";
	public final static String empty_password="empty_password";
	public final static String error_password="error_password";
	public final static String error_noextract="error_noextract";
	public final static String error_unlock="error_unlock";
	public final static String error_unlock0="error_unlock";
	public final static String unknown_error="unknown_error";
	
	
	
	
	
	private String code;
	private String[] params;

	public LockException(String code) {
		super();
		this.code = code;
	}

	public LockException(String code, String[] params) {
		super();
		this.code = code;
		this.params = params;
	}
	

	public String getCode() {
		return code;
	}


	public void setCode(String code) {
		this.code = code;
	}

	public String[] getParams() {
		return params;
	}

	public void setParams(String[] params) {
		this.params = params;
	}
	
	
	
	
}
