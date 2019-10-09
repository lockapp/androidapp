package com.rodrigo.lock.core;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import com.rodrigo.lock.core.exceptions.LockException;

public class EncryptedFileSystemHandler {
	
	private static final Map<String, WeakReference<EncryptedFileSystem>> _map= new HashMap<String, WeakReference<EncryptedFileSystem>>();	
	
	public static synchronized  EncryptedFileSystem  createEncryptedFile(String filePath, String password){		
		EncryptedFileSystem fs = EncryptedFileSystem.createEncryptedFile(filePath, password);
		_map.put(filePath, new WeakReference(fs));
		return fs;
	}
	

	public static synchronized  EncryptedFileSystem  openEncryptedFile(String filePath, String password){
		WeakReference<EncryptedFileSystem> ref = _map.get(filePath);
		EncryptedFileSystem fs = (ref != null) ? ref.get() : null;
		if (fs == null){
			fs = EncryptedFileSystem.openEncryptedFile(filePath, password);
			_map.put(filePath, new WeakReference(fs));
		}else{
			if (!fs.equalPassword(password)){
				throw new LockException(LockException.error_al_abrir_el_archivo_verifique_password);
			}
		}
		return fs;
	}
	
	
	public static synchronized void removeFromUso(String path){
		_map.remove(path);
	}
	
	
	
}
