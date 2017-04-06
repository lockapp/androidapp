package com.rodrigo.lock.core.clases;

import java.util.LinkedHashMap;
import java.util.Map;

public class FileSystemStructure {

	private int idSequence;
	private LinkedHashMap<String, LockFile> files;
	
	
	public int getIdSequence() {
		return idSequence;
	}
	public void setIdSequence(int idSequence) {
		this.idSequence = idSequence;
	}
	public LinkedHashMap<String, LockFile> getFiles() {
		return files;
	}
	public void setFiles(LinkedHashMap<String, LockFile> files) {
		this.files = files;
	}
	
	
}
