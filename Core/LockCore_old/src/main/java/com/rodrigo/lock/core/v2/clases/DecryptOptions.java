package com.rodrigo.lock.core.v2.clases;

public class DecryptOptions {
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

	public String getRutaSalida() {
		return rutaSalida;
	}

	public void setRutaSalida(String rutaSalida) {
		this.rutaSalida = rutaSalida;
	}
	
	

}
