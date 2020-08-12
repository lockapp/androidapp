package com.rodrigo.lock.core.clases;

import java.io.File;

public interface DecryptListener {
		
	public void setNumberOfFile(int i);
	//public void setProgress(Double d);
	public void newFileDecrypted(File f);
	
	

}
