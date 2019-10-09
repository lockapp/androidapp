package com.rodrigo.lock.core.data;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;

import com.google.gson.Gson;
import com.rodrigo.lock.core.clases.FileSystemStructure;
import com.rodrigo.lock.core.clases.FileType;
import com.rodrigo.lock.core.clases.LockFile;
import com.rodrigo.lock.core.data.constants.EncryptedFileConstant;
import com.rodrigo.lock.core.data.crypto.AES.CoreCrypto;
import com.rodrigo.lock.core.data.crypto.AES.CoreCrypto.AES;
import com.rodrigo.lock.core.exceptions.LockException;
import com.rodrigo.lock.core.utils.FileUtils;
import com.rodrigo.lock.core.utils.LockFileInpuStream;
import com.rodrigo.lock.core.utils.TextUtils;

/**
 * clase que maneja el acceso a los datos. La idea es grabar en un archivo temporal y hacer el replace ene l commit
 * @author Rodrigo
 *
 */

public class EncryptedFileSystemDAO {
	
	
	private AES cipher;
	private Gson converter;
	
	private String filePath;
	private FileSystemStructure  structure;
	private long posOfStructure;


	private String tempFilePath;
	private FileSystemStructure  tempStructure;
	private long tempPosOfStructure;


	public EncryptedFileSystemDAO (String filePath){
		try {
			this.cipher = CoreCrypto.getCipher();
			this.converter= new Gson();
			this.filePath = filePath;
		} catch (LockException e) {
			throw e;
		} catch (Exception e) {
			throw new LockException(LockException.error_general, e);
		}
	}

	/** paso 1 de crear nuevo archivo **/
	public void writeVersion(){
		try {
			File file= new File(filePath);
			OutputStream out = new FileOutputStream(file);
			byte[] version = new byte[EncryptedFileConstant.HEADER_VERSION_SIZE];
			Arrays.fill(version, Byte.parseByte(EncryptedFileConstant.HEADER_VERSION, 2));
			out.write(version);
			out.close();
		} catch (LockException e) {
			throw e;
		} catch (Exception e) {
			throw new LockException(LockException.cant_create, e);
		}
	}
	
	/** paso 2 de crear nuevo archivo **/
	public void createKey(String password){
		try {			
			RandomAccessFile file = new RandomAccessFile(filePath, "rw");
			file.seek(EncryptedFileConstant.START_OF_KEY);
			OutputStream os = Channels.newOutputStream(file.getChannel());
			
			cipher.makeKey();
			os =cipher.saveKey(password, os);			
			os.close();
			file.close();
		} catch (LockException e) {
			throw e;
		} catch (Exception e) {
			throw new LockException(LockException.cant_create, e);
		}
	}
	
	/** paso 3 de crear nuevo archivo **/
	public void initStructure(){
		try {			
			posOfStructure = EncryptedFileConstant.START_OF_FILES;			
			structure = new FileSystemStructure();
			structure.setFiles(new LinkedHashMap());
			structure.setIdSequence(0);			
			
			writeStructureInFile();			
		} catch (LockException e) {
			throw e;
		} catch (Exception e) {
			throw new LockException(LockException.cant_create, e);
		}	
	}
	

	/** paso 1 de abrir archivo existente **/
	public void checkVersion(){
		try {			
			File file =  new File(filePath);
			if(!file.exists()) {     			
				throw new LockException(LockException.not_found);
			}		
			FileInputStream in = new FileInputStream(file);
			
			byte[] version = new byte[1];
			in.read(version);
			if (version[0] > EncryptedFileConstant.HEADER_VERSION_IN_BYTE) {
				throw new LockException(LockException.error_version);
			}			
			in.close();
		} catch (LockException e) {
			throw e;
		} catch (Exception e) {
			throw new LockException(LockException.cant_create, e);
		}	
	}
	
	
	/** paso 2 de abrir archivo existente **/
	public void loadKey(String password){
		try {			
			RandomAccessFile file = new RandomAccessFile(filePath, "rw");
			file.seek(EncryptedFileConstant.START_OF_KEY);
			InputStream in = Channels.newInputStream(file.getChannel());
			cipher.loadKey(in, password);	
			in.close();
			file.close();
		} catch (LockException e) {
			throw e;
		} catch (Exception e) {
			throw new LockException(LockException.error_al_abrir_el_archivo_verifique_password);
		}	
	}

	
	/** paso 3 de abrir archivo existente **/
	public void loadStructure(){
		try {			
			RandomAccessFile file = new RandomAccessFile(filePath, "r");
			file.seek(EncryptedFileConstant.START_OF_INDEX_STRUCTURE);
			InputStream in = Channels.newInputStream(file.getChannel());
			byte[] buff = new byte[EncryptedFileConstant.LONG_IN_BYTE];
			in.read(buff);
			posOfStructure = FileUtils.bytesToLong(buff);

			file.seek(posOfStructure);
			in = new CipherInputStream(in, cipher.getCiphertoDec(in));			
			
			byte[] structureInByte = FileUtils.toByteArray(in);
			structure= converter.fromJson(new String(structureInByte), FileSystemStructure.class);
			
			in.close();
			file.close();
		} catch (LockException e) {
			throw e;
		} catch (Exception e) {
			throw new LockException(LockException.error_al_abrir_el_archivo_verifique_password);
		}	
	}



	public LockFile addEncryptedFileFromFileToTemp(InputStream iput, String fullPath){
		try {
			String extension = FileUtils.getExtensionFile(fullPath);
			
			LockFile encryptedFile = new LockFile();
			encryptedFile.setId(nextIdTemp(extension));		
			encryptedFile.setType(FileType.FILE);	
			encryptedFile.setStart(tempPosOfStructure);
			encryptedFile.setFullPath(fullPath);
			tempStructure.getFiles().put(encryptedFile.getId(), encryptedFile);

			addFileStreamToEndOfTemp(encryptedFile, iput);		


			/*if (preview!= null){
				LockFile previewFile = new LockFile();
				previewFile.setId(nextIdTemp(extension));		
				previewFile.setType(FileType.PREVIEW);	
				encryptedFile.setStart(tempPosOfStructure);
				encryptedFile.setPreviewId(previewFile.getId());
				tempStructure.getFiles().put(previewFile.getId(), previewFile);
				
				addFileStreamToEndOfTemp(previewFile, preview);			

			}*/
			
			return encryptedFile;
			
		} catch (LockException e) {
			throw e;
		} catch (Exception e) {
			throw new LockException(LockException.no_se_pudo_agregar_el_archivo);
		}

	}
	
	public LockFile addPreviewToTemp(LockFile father,  InputStream preview){
			LockFile previewFile = addEncryptedFileFromFileToTemp(preview, father.getFullPath());			
			previewFile.setType(FileType.PREVIEW);	
			father.setPreviewId(previewFile.getId());			
			return previewFile;		
	}
	
	
	
	
	
	public LockFile addFolderToTemp(String fullPath){
		LockFile encryptedFile = new LockFile();
		encryptedFile.setId(nextIdTemp(""));		
		encryptedFile.setType(FileType.FOLDER);
		encryptedFile.setFullPath(fullPath);
		tempStructure.getFiles().put(encryptedFile.getId(), encryptedFile);
		return encryptedFile;
	}
	
	
	
	public void extractFile(String id, OutputStream out){
		try {
			LockFile lockFile = structure.getFiles().get(id);
			if (lockFile!= null /*&& lockFile.getType()!= FileType.FOLDER*/){
				
				FileInputStream file = new FileInputStream(new File(filePath));
				LockFileInpuStream input = new LockFileInpuStream(file, lockFile.getStart(),  lockFile.getSize());				
				InputStream cipherInputStream = new CipherInputStream(input, cipher.getCiphertoDec(input));
				cipherInputStream = new  BufferedInputStream(cipherInputStream);
				
				out = new BufferedOutputStream(out);
				FileUtils.copy(cipherInputStream, out);
				
				out.close();
				input.close();
			}
		} catch (LockException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new LockException(LockException.no_se_pudo_abrir_el_archivo);
		}
	}
	
	
	
	
	private void addFileStreamToEndOfTemp(LockFile fileDescriptor, InputStream iput) throws Exception{
		//RandomAccessFile file = new RandomAccessFile(this.tempFilePath, "rw");
		//file.setLength(file.getFilePointer());			
		//file.seek(tempPosOfStructure);
		 FileChannel file = new FileOutputStream(this.tempFilePath, true).getChannel();
		 file.truncate(tempPosOfStructure);
		 //file.position(tempPosOfStructure)
		 
		//fileDescriptor.setStart(file.getFilePointer());
		 fileDescriptor.setStart(tempPosOfStructure);
		 
		 
		OutputStream fileOut = Channels.newOutputStream(file);
		CipherOutputStream cipherOut = new CipherOutputStream(fileOut, cipher.getCiphertoEnc(fileOut));
		BufferedOutputStream out = new BufferedOutputStream(cipherOut);
		//OutputStream out= cipherOut;

		BufferedInputStream sourceStream = new BufferedInputStream(iput,  FileUtils.BUFFER_SIZE);

		FileUtils.copy(sourceStream, out);
		
		sourceStream.close();		
		
		//out.flush();
		//cipherOut.flush();
		//fileOut.flush();
		
		out.close();
		//cipherOut.close();
		//fileOut.close();
		file.close();
		
		tempPosOfStructure = new File (tempFilePath).length();
		//tempPosOfStructure = file.size();
		//file.close();
		
		fileDescriptor.setSize(tempPosOfStructure- (fileDescriptor.getStart() ));
	}

	
	

	
	public void delteFilesInTemp(Set<String> todelete) throws IOException{
		FileInputStream originalToRead = new FileInputStream(this.filePath);
		FileOutputStream tempToSave = new FileOutputStream(this.tempFilePath);
		try{										
			FileUtils.copy(originalToRead, tempToSave, EncryptedFileConstant.START_OF_FILES);		
			
			List<LockFile> files =  new ArrayList (tempStructure.getFiles().values());					
			//se ordenan por ubicacion los archivos
			Collections.sort(files, new Comparator<LockFile>() {
			    @Override
			    public int compare(LockFile o1, LockFile o2) {
			        return o1.getStart().compareTo(o2.getStart());
			    }
			});			
			
			long posActual=EncryptedFileConstant.START_OF_FILES;			
			//va a recorrer todos los archivos y los va corriendo
			for (int iter = 0; iter< files.size(); iter++){
				LockFile actual = files.get(iter);
				//si es elemento a eliminar calcula cuanto hay que correr
				if (todelete.contains(actual.getId())){
					originalToRead.skip(actual.getSize());
					//offset +=  actual.getSize();
					tempStructure.getFiles().remove(actual.getId());
				}else {					
					//setea la ueva posicion inicial
					actual.setStart(posActual);
					//se copia el contenido del archivo al temporal 
					FileUtils.copy(originalToRead, tempToSave,actual.getSize());
					//actualiza por donde va
					posActual = posActual +actual.getSize();			
				}
			}
			
			tempPosOfStructure = posActual;
		
		} catch (LockException e) {
			throw e;
		} catch (Exception e) {
			throw new LockException(LockException.no_se_pudo_eliminar_el_archivo);
		} finally {
			tempToSave.flush();
            try {
            	tempToSave.getFD().sync();
            } catch (IOException e) {
            }
            tempToSave.close();
            originalToRead.close();        
		}
	}
	
	
	
	public void delteFoldersInTempStructure(Set<String> listfolderID) throws Exception{
		for (String folderId: listfolderID){
			this.tempStructure.getFiles().remove(folderId);
		}
	}
		
	
	
	public void writeStructureInFile() throws Exception{
		writeStructureInPath(filePath, posOfStructure, structure );
	}

	
	
	public void writeStructureInTempFile() throws Exception{
		writeStructureInPath(tempFilePath, tempPosOfStructure, tempStructure);
	}
	
	private void writeStructureInPath(String path, long posOfStructure, FileSystemStructure structure) throws Exception{
		RandomAccessFile file = new RandomAccessFile(path, "rw");
		file.seek(EncryptedFileConstant.START_OF_INDEX_STRUCTURE);
		file.write(FileUtils.longToBytes(posOfStructure));
		
		file.seek(posOfStructure);
		file.setLength(file.getFilePointer());		
		
		OutputStream out = Channels.newOutputStream(file.getChannel());
		out = new CipherOutputStream(out, cipher.getCiphertoEnc(out));
		out = new BufferedOutputStream(out);
		out.write(converter.toJson(structure).getBytes());		
		out.close();
		file.close();
	}


	private int nextId(){
		structure.setIdSequence(structure.getIdSequence() + 1);
		return structure.getIdSequence();		
	}

	private String nextIdTemp(String extension){
		tempStructure.setIdSequence(tempStructure.getIdSequence() + 1);
		return tempStructure.getIdSequence() + extension;		
	}

	public FileSystemStructure getStructure(){
		return this.structure;
	}
	public FileSystemStructure getTempStructure(){
		return this.tempStructure;
	}
	
	public void copyActualContentToTempFile() throws Exception{		
		FileInputStream actual = new FileInputStream(new File (this.filePath));
		FileOutputStream temp = new FileOutputStream(new File (this.tempFilePath));
		try{
			FileUtils.copy(actual, temp);		
		} finally {
			temp.flush();
            try {
            	temp.getFD().sync();
            } catch (IOException e) {
            }
            temp.close();
            actual.close();
        }
	}

	public void copyActualStructureToTempStructure(){
		tempPosOfStructure = posOfStructure;
		
		tempStructure = new FileSystemStructure();
		tempStructure.setIdSequence(structure.getIdSequence());
		tempStructure.setFiles(new LinkedHashMap<String, LockFile>());
		Iterator<LockFile> iterator = structure.getFiles().values().iterator();
        while(iterator.hasNext()){
        	LockFile f = iterator.next();
        	LockFile nuevo =new LockFile();
        	nuevo.setFullPath(f.getFullPath());
        	nuevo.setId(f.getId());
        	nuevo.setPreviewId(f.getPreviewId());
        	nuevo.setSize(f.getSize());
        	nuevo.setStart(f.getStart());
        	nuevo.setType(f.getType());
        	tempStructure.getFiles().put(nuevo.getId(),nuevo);
        }
	}
	
	public void convertTempStructureInOriginal(){
		this.structure = this.tempStructure;
		this.posOfStructure = this.tempPosOfStructure;
	}
	
	
	public void convertTempFileInOriginalFile(){
		File original= new File(this.filePath);
		original.delete();
		
		File nuevo = new File(this.tempFilePath);
		nuevo.renameTo(new File(this.filePath));
		
	}
	
	public void initTempFilePath(){
		File file = new File (this.filePath);
		this.tempFilePath=FileUtils.getTempPathFileForFile(file);
	}

	
	public void deleteTempFile() throws IOException{
		if (!TextUtils.isEmpty(this.tempFilePath)){			
			File file = new File (this.tempFilePath);
			if (file.exists()){
				FileUtils.delete(file);
			}
		}
	}
	
//	public void beginTransactionToWriteInTemp() throws Exception{
//		initTempFilePath();
//		copyActualContentToTempFile();
//		copyActualStructureToTempStructure();
//	}
//
//	public void commitTransactionInTemp() throws Exception{
//		writeStructureInTempFile();
//		convertTempFileInOriginalFile();
//		convertTempStructureInOriginal();
//	}
}
