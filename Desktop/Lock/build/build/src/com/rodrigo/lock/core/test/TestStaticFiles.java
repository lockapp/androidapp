package com.rodrigo.lock.core.test;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import junitx.framework.FileAssert;

import org.junit.BeforeClass;
import org.junit.Test;

import com.rodrigo.lock.core.EncryptedFileSystem;
import com.rodrigo.lock.core.EncryptedFileSystemHandler;
import com.rodrigo.lock.core.datatype.AddFileListener;
import com.rodrigo.lock.core.utils.FileUtils;

public class TestStaticFiles {

	public static final String password = "123";
	public static final String fixedFiles = "D:\\testLock\\fixedFiles";
	public static final String encryptedFile = "D:\\testLock\\TestStaticFiles.lock";
	public static final String extractedFiles = "D:\\testLock\\extractedFiles";

	
	private static List<File> origilanles=new LinkedList<File>();
	
	//con zip
	//testEncrypt:  6652937303 ns
	//testDecrypt:  4256530850 ns
	//usando tar
	//testEncrypt:  3386244636 ns
	//testDecrypt:  3214025063 ns
	//metodo propio
	//testEncrypt:  3958168111 ns
	//testDecrypt:  4423815861 ns
					
	
	@BeforeClass
	public static void init() throws Exception {	
		FileUtils.delete(new File(encryptedFile));
		FileUtils.delete(new File(extractedFiles));
		
		File folder = new File(fixedFiles);
		for (File fileEntry : folder.listFiles()) {
			origilanles.add(fileEntry);
	    }			
	}

	
	@Test
	public  void test () throws Exception{								
		System.out.println("-------------TestStaticFiles-------------");
		
		System.out.println("va a a;adir los archivos");
		EncryptedFileSystem controller =  EncryptedFileSystemHandler.createEncryptedFile(encryptedFile, password);
		long start = System.nanoTime();    
		controller.addFile(new AddFileListener(), origilanles);
		long elapsedTime = (System.nanoTime() - start) ;
		System.out.println("testEncrypt: "  + elapsedTime + " ns");
		
		
		System.out.println("va a extraer");
		start = System.nanoTime();    
		controller.extractAllFilesAndFolders(new File(extractedFiles));
		elapsedTime = (System.nanoTime() - start) ;
		System.out.println("testDecrypt: "  + elapsedTime + " ns");
		
		System.out.println("va a comprarar lso archivos");
		for (File f1:origilanles){	
			//File f1 = new File(randomFiles + File.separator + i);
			File f2 = new File(extractedFiles + File.separator + f1.getName());
			String message= "Error> tienen contenido diferente, file1:" + f1.getAbsolutePath()+ ",  file 2:" + f2.getAbsolutePath();
			FileAssert.assertBinaryEquals(message, f1, f2);
		}
						
	}
	
}
