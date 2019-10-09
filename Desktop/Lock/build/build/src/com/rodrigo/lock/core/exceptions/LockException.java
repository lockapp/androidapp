package com.rodrigo.lock.core.exceptions;

import java.util.LinkedList;
import java.util.List;

//LinkedList<String> lparam = new LinkedList<>();
//lparam.add(toEncrypt);
//throw new LockException(LockException.error_open, lparam);

public class LockException extends RuntimeException{
	//no se pudo crear el archivo
	public final static String error_general="err_error_general";
	public final static String cant_create="err_cant_create";
	public final static String el_archivo_ya_existe="err_the_file_already_exists";
	public final static String metodo_cifrado_no_soportado_por_os="err_unsupported_encryption_method";
	public final static String no_se_pudo_agregar_el_archivo="err_failed_to_add_the_file";
	public final static String error_al_abrir_el_archivo_verifique_password="err_unable_to_open_file_Verify_the_password";
	public final static String no_se_pudo_eliminar_el_archivo="err_could_not_delete_the_file";
	public final static String no_se_pudo_abrir_el_archivo="err_could_not_open_the_file";
	public final static String not_found="err_not_found";
	public final static String error_version="error_version";

//	public final static String file_not_found_2="error_notfound2";
//	public final static String error_lock2="error_lock2";
//	public final static String error_delete2="error_delete2";	
//	public final static String error_noextract="error_noextract";
//	public final static String error_unlock="error_unlock";
//	public final static String unknown_error="unknown_error";
	//solo para migracion
	public final static String error_open="error_open";
	public final static String error_nosecureview="error_nosecureview";
	public final static String error_defeated="error_defeated";
	public final static String empty_password="empty_password";
	public final static String error_password="error_password";
	public final static String error_unlock0="error_unlock";

	
	
	private String code;
	private String[] params;

	public LockException(String code, Exception e) {
		super(e);
		this.code = code;
	}

	public LockException(String code, String[] params,  Exception e) {
		super(e);
		this.code = code;
		this.params = params;
	}
	
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
