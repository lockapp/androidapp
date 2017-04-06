package com.rodrigo.lock.core.datatype;

public class AddFileListener {
	private int adeedFiles =0;
	
	public void fileAdded(){
		adeedFiles++;
	}
	
	public int getAddedFiles(){
		return adeedFiles;
	}
}
