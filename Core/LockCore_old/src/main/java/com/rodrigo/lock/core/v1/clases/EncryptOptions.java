package com.rodrigo.lock.core.v1.clases;

import java.util.Date;

public class EncryptOptions {
	private String password;
	private String pathToSave;
	
	

	
	
	private Date fechaVencimiento = null;
	private Boolean soloAbrirEnEsteDispositivo=false;
	//private Boolean prohibidoExtraer =false;
	private Boolean dejarCopiaSinBloquear =false;
	//dejar en null y funciona bien
	private String outFileName = null;
	
	

	public String mergeIdInPassword(String pass) {
		// TODO Auto-generated method stub
		return pass;
	}
	
	public Date getFechaVencimiento() {
		return fechaVencimiento;
	}
	public void setFechaVencimiento(Date fechaVencimiento) {
		this.fechaVencimiento = fechaVencimiento;
	}
	public Boolean getSoloAbrirEnEsteDispositivo() {
		return soloAbrirEnEsteDispositivo;
	}
	public void setSoloAbrirEnEsteDispositivo(Boolean soloAbrirEnEsteDispositivo) {
		this.soloAbrirEnEsteDispositivo = soloAbrirEnEsteDispositivo;
	}
//	public Boolean getProhibidoExtraer() {
//		return prohibidoExtraer;
//	}
//	public void setProhibidoExtraer(Boolean prohibidoExtraer) {
//		this.prohibidoExtraer = prohibidoExtraer;
//	}
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
