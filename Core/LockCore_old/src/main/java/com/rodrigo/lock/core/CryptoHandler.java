package com.rodrigo.lock.core;

import java.io.File;
import java.util.List;

import com.rodrigo.lock.core.enums.CryptoAction;

public abstract  class CryptoHandler {
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
