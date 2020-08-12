package com.rodrigo.lock.core.v2.clases;

import java.util.Date;

public class EncryptOptions {
	private String password;
	private String pathToSave;
	
	private Boolean dejarCopiaSinBloquear =false;
	//dejar en null y funciona bien
	private String outFileName = null;
	
	

	public String mergeIdInPassword(String pass) {
		// TODO Auto-generated method stub
		return pass;
	}
	public Boolean getDejarCopiaSinBloquear() {
		return dejarCopiaSinBloquear;
	}
	public void setDejarCopiaSinBloquear(Boolean dejarCopiaSinBloquear) {
		this.dejarCopiaSinBloquear = dejarCopiaSinBloquear;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getPathToSave() {
		return pathToSave;
	}
	public void setPathToSave(String pathToSave) {
		this.pathToSave = pathToSave;
	}
	public String getOutFileName() {
		return outFileName;
	}
	public void setOutFileName(String outFileName) {
		this.outFileName = outFileName;
	}

	
	
	
	
	
}
