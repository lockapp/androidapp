package com.rodrigo.lock.core.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import junitx.framework.FileAssert;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.anarsoft.vmlens.concurrent.junit.ConcurrentTestRunner;
import com.anarsoft.vmlens.concurrent.junit.ThreadCount;
import com.rodrigo.lock.core.EncryptedFileSystem;
import com.rodrigo.lock.core.EncryptedFileSystemHandler;
import com.rodrigo.lock.core.clases.LockFile;
import com.rodrigo.lock.core.datatype.AddFileListener;
import com.rodrigo.lock.core.utils.FileUtils;

@RunWith(ConcurrentTestRunner.class)
public class TestConcurrencia {
	public static final String password = "123";
	public static final String randomFiles = "D:\\testLock\\randomFiles";
	public static final String encryptedFile = "D:\\testLock\\TestConcurrencia.lock";
	public static final String extractedFiles = "D:\\testLock\\extractedFiles";
	
	public static final int numberOfFileToTest=10;
	public static final long maxSizeInByte=1024*1024*5;//1024*1024*512;//0.5gb cada archivo
	public static final long minSizeInByte=1;
	

	public static EncryptedFileSystem controller ;

	private static AtomicInteger count = new AtomicInteger(0);
	
	@BeforeClass
	public static void init() throws Exception {	
		System.out.println("-------------testConcurrencia-------------");
		System.out.println("va a limpia los archivos");
		FileUtils.delete(new File(randomFiles));
		FileUtils.delete(new File(encryptedFile));
		FileUtils.delete(new File(extractedFiles));
		
		System.out.println("va a crear los archivos randomicos");
		controller =  EncryptedFileSystemHandler.createEncryptedFile(encryptedFile, password);
		List<File>lista= new LinkedList<>();
		for (int i =0; i< numberOfFileToTest; i++){	
			String fileName = i+"";
			lista.add(TestUtils.createRandomFile(randomFiles, fileName, minSizeInByte, maxSizeInByte));
		}		

		System.out.println("va a a;adir los archivos");
		controller.addFile(new AddFileListener(), lista);
	}

	
	@Test
	@ThreadCount(20)
	public  void testRead () {			
		int id = count.incrementAndGet();					
		try {			
	    	System.out.println("---> ["+ System.nanoTime() + "] comienza hilo de lectura " + id);
	    	
	    	List<LockFile>  l =controller.getFilesAndFolders();
			LockFile file = l.get(id%l.size());
			
	    	int name =id+numberOfFileToTest;
	    	File f2 = new File(extractedFiles + File.separator + name);
	    	f2.getParentFile().mkdirs();
	    	OutputStream o = new FileOutputStream(f2);
			
			File f1 = new File(randomFiles + File.separator +file.getFullPath());
			controller.extractFile(file.getId(),o );
			String message= "Error> tienen contenido diferente, file1:" + f1.getAbsolutePath()+ ",  file 2:" + f2.getAbsolutePath();
			FileAssert.assertBinaryEquals(message, f1, f2);
			System.out.println("---> ["+ System.nanoTime() + "] termina hilo de lectura " + id);
	    	} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
	    		System.out.println("---> ["+ System.nanoTime() + "] ERROR de lectura " + id);
				e.printStackTrace();
			}		    
	}
		


	

	@Test
	@ThreadCount(10)
	public  void testWrite (){		
		int id=count.incrementAndGet();						
    	try {
    		System.out.println("---> ["+ System.nanoTime() + "] comienza hilo de escritura " + id);
	    	List<File>lista= new LinkedList<>();
	    	int name =id+numberOfFileToTest;
	    	String fileName = name+"";
			lista.add(TestUtils.createRandomFile(randomFiles, fileName, minSizeInByte, maxSizeInByte));				
			controller.addFile(new AddFileListener(), lista);
			System.out.println("---> ["+ System.nanoTime() + "] termina hilo de escritura " + id);
    	} catch (Exception e) {
			// TODO Auto-generated catch block	   
    		System.out.println("---> ["+ System.nanoTime() + "] ERROR de escritura " + id);
			e.printStackTrace();
		}
		
						
	}
	
	
	
	
	@After
	public void assertCount() throws Exception	{
		System.out.println("----termino todos los hilos ----");	
		FileUtils.delete(new File(extractedFiles));
		controller.extractAllFilesAndFolders(new File(extractedFiles));
		
		List<LockFile>  l =controller.getFilesAndFolders();
		for (LockFile lf: l){	
			System.out.println("va a comprarar " + lf.getId());
			File f1 = new File(randomFiles + File.separator + lf.getFullPath());
			File f2 = new File(extractedFiles + File.separator +  lf.getFullPath());
			String message= "Error> tienen contenido diferente, file1:" + f1.getAbsolutePath()+ ",  file 2:" + f2.getAbsolutePath();
			FileAssert.assertBinaryEquals(message, f1, f2);
		}
	}
	
	
	
}
