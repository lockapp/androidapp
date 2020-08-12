package com.rodrigo.lock.core.v1.clases;

public class DecryptOptions {
	//si lo esta viendo en vista segura+_
	boolean usarVistaSegura;
	String password;
	boolean conservarOriginal;
	//se tendria que setear como inFile.getParent()
	String rutaSalida;
	
	

	public String mergeIdInPassword(String pass) {
		// TODO Auto-generated method stub
		return pass;
	}
	
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public boolean isConservarOriginal() {
		return conservarOriginal;
	}
	public void setConservarOriginal(boolean conservarOriginal) {
		this.conservarOriginal = conservarOriginal;
	}

	public boolean isUsarVistaSegura() {
		return usarVistaSegura;
	}

	public void setUsarVistaSegura(boolean usarVistaSegura) {
		this.usarVistaSegura = usarVistaSegura;
	}

	public String getRutaSalida() {
		return rutaSalida;
	}

	public void setRutaSalida(String rutaSalida) {
		this.rutaSalida = rutaSalida;
	}
	
	

}
