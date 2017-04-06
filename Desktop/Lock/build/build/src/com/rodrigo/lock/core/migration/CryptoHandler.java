package com.rodrigo.lock.core.migration;


public class CryptoHandler {
	protected CryptoAction accion ;
	
	protected CryptoHandler (CryptoAction accion){
		this.accion=accion;
	}
	
	protected CryptoHandler (){
	}
	
	public  CryptoAction getAccion(){
		return accion;
	}
	
}
