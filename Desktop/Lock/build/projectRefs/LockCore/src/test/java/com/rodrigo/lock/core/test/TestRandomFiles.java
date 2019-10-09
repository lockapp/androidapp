package com.rodrigo.lock.core.test;

import static org.junit.Assert.*;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import junitx.framework.FileAssert;

import org.omg.CosNaming.IstringHelper;

import com.rodrigo.lock.core.EncryptedFileSystem;
import com.rodrigo.lock.core.EncryptedFileSystemHandler;
import com.rodrigo.lock.core.clases.LockFile;
import com.rodrigo.lock.core.data.EncryptedFileSystemDAO;
import com.rodrigo.lock.core.datatype.AddFileListener;
import com.rodrigo.lock.core.utils.FileUtils;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestRandomFiles {

	public static final String password = "123";
	public static final String randomFiles = "D:\\testLock\\randomFiles";
	public static final String encryptedFile = "D:\\testLock\\TestRandomFiles.lock";
	public static final String extractedFiles = "D:\\testLock\\extractedFiles";
	
	public static final int numberOfFileToTest=10;
	public static final long maxSizeInByte=1024*1024*10;//1024*1024*512;//0.5gb cada archivo
	public static final long minSizeInByte=1;
	
	
	
	
	@Test
	public  void test1EncryptAndDecrypt () throws Exception{								
		System.out.println("-------------testEncryptAndDecrypt-------------");
		System.out.println("va a limpia los archivos");
		FileUtils.delete(new File(randomFiles));
		FileUtils.delete(new File(encryptedFile));
		FileUtils.delete(new File(extractedFiles));
		
		System.out.println("va a crear los archivos randomicos");
		EncryptedFileSystem controller =  EncryptedFileSystemHandler.createEncryptedFile(encryptedFile, password);
		List<File>lista= new LinkedList<>();
		for (int i =0; i< numberOfFileToTest; i++){	
			String fileName = i+"";
			lista.add(TestUtils.createRandomFile(randomFiles, fileName, minSizeInByte, maxSizeInByte));
		}

		System.out.println("va a a;adir los archivos");
		controller.addFile(new AddFileListener(), lista);
		
		
		System.out.println("va a desencriptar los archivos");
		EncryptedFileSystemHandler.removeFromUso(encryptedFile);
		EncryptedFileSystem controller2 = EncryptedFileSystemHandler.openEncryptedFile(encryptedFile, password);
		controller2.extractAllFilesAndFolders(new File(extractedFiles));
		
		System.out.println("va a comprarar lso archivos");
		for (int i =0; i< numberOfFileToTest; i++){	
			File f1 = new File(randomFiles + File.separator + i);
			File f2 = new File(extractedFiles + File.separator + i);
			String message= "Error> tienen contenido diferente, file1:" + f1.getAbsolutePath()+ ",  file 2:" + f2.getAbsolutePath();
			FileAssert.assertBinaryEquals(message, f1, f2);
		}
						
	}
	
	
	@Test
	public  void test2DeleteFiles () throws Exception{
		System.out.println("-------------deleteFiles-------------");
		System.out.println("va a eliminar lo extraido");			
		FileUtils.delete(new File(extractedFiles));
		
		EncryptedFileSystem controller = EncryptedFileSystemHandler.openEncryptedFile(encryptedFile, password);
		
		System.out.println("va a consultar lso archivos");
		List<LockFile> list =controller.getFilesAndFolders();

		LockFile l0 = list.get(0);
		LockFile l3 = list.get(3);
		LockFile lmax = list.get(list.size()-1);
		
		System.out.println("va a eliminar 3 archivos de una lista que tiene " +  list.size());
		controller.deleteFiles(l3.getId(), lmax.getId(), l0.getId());
		
		System.out.println("va comprar la cantidad de archivos en la bobeda");
		list =controller.getFilesAndFolders();
		assertEquals("la cantidad de archivos en la bobeda es distitna a la esperada", numberOfFileToTest-3, list.size());
		
		System.out.println("va a extraer todos los archivos d ela bobeda");
		controller.extractAllFilesAndFolders(new File(extractedFiles));
		
		System.out.println("va acomprarar los archivos");
		for (int i =1; i< numberOfFileToTest; i++){	
			String fileName = i+"";
			if (!l0.getFullPath().equals(fileName) && !l3.getFullPath().equals(fileName) 
					&& !lmax.getFullPath().equals(fileName)){
				File f1 = new File(randomFiles + File.separator + i);
				File f2 = new File(extractedFiles + File.separator + i);
				String message= "Error> tienen contenido diferente, file1:" + f1.getAbsolutePath()+ ",  file 2:" + f2.getAbsolutePath();
				FileAssert.assertBinaryEquals(message, f1, f2);
			
			}
		}						
		
	}
	
	
	


	

}
