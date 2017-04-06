package com.rodrigo.lock.core.test;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;

public class TestUtils {

	public static final int BUFFER_SIZE = 2048; //8192, 2048
	
	public static File createRandomFile(String baseDir, String name, long minSizeInByte, long maxSizeInByte) throws Exception{
		Random random = new Random();
		
		File f = new File(baseDir + File.separator + name);
		if (f.exists()){
			f.delete();
		}
		f.getParentFile().mkdirs();
		
		//calcula el tama;o randomico del archivo
		long sizeOfByte = minSizeInByte+((long)(random.nextDouble()*(maxSizeInByte-minSizeInByte)));
		//System.out.println("va a vrear el archivo> " + name + " de largo " + sizeOfByte );
		
		OutputStream out = new FileOutputStream(f);
		out = new BufferedOutputStream(out);
		
		long total = 0;
		
		byte[] data = new byte[BUFFER_SIZE];
		while (true) {
			int toRead =(int) ((total + BUFFER_SIZE > sizeOfByte) ? (sizeOfByte -total ): BUFFER_SIZE);
			if (toRead!= BUFFER_SIZE){
				data = new byte[toRead];
			}			
			random.nextBytes(data);
			out.write(data);
			total = total + toRead;
			if (total == sizeOfByte){
				break;
			}
		}
		out.close();
		

		//System.out.println("creado el archivo archivo> " + f.getName() + " de largo " + f.length() );
		return f;
		
	}
	
	
	
	public static boolean sameContent(File file1, File file2) throws IOException {
	    if (file1.length()!= file2.length()){
	    	System.out.println("largos distintos file1.length()= " + file1.length() + ", file2.length()=" + file2.length());
	    	return false;
	    }
	        
	    try (
	    	 InputStream is1 = new FileInputStream(file1);
	         InputStream is2 = new FileInputStream(file2)) {
	        // Compare byte-by-byte.
	        // Note that this can be sped up drastically by reading large chunks
	        // (e.g. 16 KBs) but care must be taken as InputStream.read(byte[])
	        // does not neccessarily read a whole array!
	        int data;
	        long iter =0;
	        while ((data = is1.read()) != -1){
	        	iter++;	        	
	            if (data != is2.read()){
	            	System.out.println("byte distinto en posicion> " + iter + " disponibles " + is2.available() + "largo: " + file1.length());
	            	return false;
	            }
	        }
	                
	    }

	    return true;
	}
}
