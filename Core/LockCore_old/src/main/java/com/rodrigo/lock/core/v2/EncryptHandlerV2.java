package com.rodrigo.lock.core.v2;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;





import com.rodrigo.lock.core.CryptoHandler;
import com.rodrigo.lock.core.Utils.DateUtils;
import com.rodrigo.lock.core.Utils.FileUtils;
import com.rodrigo.lock.core.Utils.TextUtils;
import com.rodrigo.lock.core.archives.tar2.TarArchiveEntry;
import com.rodrigo.lock.core.archives.tar2.TarArchiveOutputStream;
import com.rodrigo.lock.core.clases.EncryptListener;
import com.rodrigo.lock.core.crypto.AES.CoreCrypto;
import com.rodrigo.lock.core.crypto.AES.CoreCryptoV0;
import com.rodrigo.lock.core.crypto.AES.CoreCryptoV1;
import com.rodrigo.lock.core.crypto.AES.Crypto;
import com.rodrigo.lock.core.crypto.AES.CoreCryptoV0.AES;
import com.rodrigo.lock.core.enums.CryptoAction;
import com.rodrigo.lock.core.v2.clases.EncryptOptions;

import exceptions.LockException;

public class EncryptHandlerV2 extends CryptoHandler{
	private EncryptOptions opciones;
	private String toEncrypt;// outFS;
	private EncryptListener listener;
	
	protected List<File> archivosIn;

	
	public List<File>  getFilestoEncrypt(){
		return archivosIn;
	}
	
	
	
	public EncryptHandlerV2(List<File> archivosIn) {
		super(CryptoAction.TO_ENCRYPT);
		this.archivosIn = archivosIn;
	}
	
	public void init(EncryptOptions opciones ){
		this.opciones = opciones;
		
		 for (File inF: this.archivosIn){
             if (!inF.exists()) {
            	 String[] lparam = {inF.getName()};
            	 throw new LockException(LockException.file_not_found_2,  lparam);
             }
         }

         //se pone el nombre del primero            
         String name  = opciones.getOutFileName();
         String path = opciones.getPathToSave();
         toEncrypt = path + File.separator + name +"."+FileUtils.ENC_EXTENSION_2;

         if ((new File(toEncrypt)).exists()) {
        	 toEncrypt = FileUtils.createNewFileNameInPath(path + File.separator, name, FileUtils.ENC_EXTENSION_2);
         }		
	}
		
	
	private TarArchiveOutputStream  output = null;
	private OutputStream out = null;
	
	
	public String encrypt(EncryptListener listener) throws Exception {
		this.listener = listener;
        abrirArchivoSimple();
        //grabarCabezales();
                
        CoreCrypto algo = Crypto.createCryptoV1();   
        out = new CipherOutputStream(out,  algo.getCiphertoEncZip(out, opciones.getPassword()));
        
        output = new TarArchiveOutputStream(new BufferedOutputStream(out));
        //output.setLevel(Deflater.NO_COMPRESSION);
        output.setLongFileMode(TarArchiveOutputStream.LONGFILE_POSIX);
        output.setBigNumberMode(TarArchiveOutputStream.BIGNUMBER_POSIX);
        output.setAddPaxHeadersForNonAsciiNames(true);
        
        grabarListaArchivos();

        if (!opciones.getDejarCopiaSinBloquear()){
            finalizarArchivos();
        }

        return toEncrypt;
    }
	

    private void grabarListaArchivos() throws Exception {
        try {
            byte[] buffer = new byte[1024];
            for (File file : this.archivosIn) {
            	
            	if (file.isDirectory()){
            		List <String> fileList = new LinkedList<String> ();
            		generateFileListForFolder(file, file, fileList); 
            		
            		for (String hijo : fileList) {
            			grabarOutput(hijo, new File(file.getParent() + File.separator + hijo), buffer);
            		}
            		
            	}else{
            		grabarOutput(file.getName(),  file, buffer);
            	}
            	
            	/*           	
                //System.out.println("File Added : " + file);
                ZipEntry ze = new ZipEntry(file.getFile().getName());
                output.putNextEntry(ze);
                //BufferedOutputStream out = new BufferedOutputStream(output);

                FileInputStream inf = new FileInputStream(file.getFile());
                BufferedInputStream in = new BufferedInputStream(inf);

                int len;
                while ((len = in.read(buffer,0,1024)) >= 0) {
                    output.write(buffer, 0, len);
                   // avanza = avanza + len;
                }
                inf.close();
                //feedback.setProgress((int) ((avanza * 100) / tamF) % 99);
                 * */
                 
            }
            //output.closeEntry();
            //output.closeArchiveEntry();
            output.close();


        } catch (Exception ex) {
            try {
                if (output != null) {
                    output.closeArchiveEntry();
                    output.close();
                    out.close();
                }
                //no se a a;adido ala galeria basta elimnar asi
                FileUtils.delete(new File(toEncrypt));
            } catch (Exception e) {
            }
            
            throw new LockException(LockException.error_lock2);
        }
    }


    int numberOfFiles = 0;
    
 
    static final int BUFFER = 2048;

    private void grabarOutput(String name, File file, byte[] buffer) throws Exception{    	
    	
//        //System.out.println("File Added : " + file);
//    	TarArchiveEntry  ze = new TarArchiveEntry (in, name); 
//    	//ze.setSize(size);
//
//        output.putArchiveEntry(ze);
//        
//        //BufferedOutputStream out = new BufferedOutputStream(output);
//        FileInputStream inf = new FileInputStream(in);
//        BufferedInputStream sourceStream = new BufferedInputStream(fi,     		
//        		      BUFFER);
//
//
//        int len;
//        while ((len = inf.read(buffer,0,1024)) >= 0) {
//        	output.write(buffer, 0, len);
//           // avanza = avanza + len;
//        }
//        inf.close();
//        
//        
//        
                
        /**
         * relativize is used to to add a file to a tar, without
         * including the entire path from root.
         * .getParentFile().toURI().relativize(files[i].toURI())
          .getPath()
         **/

        TarArchiveEntry entry = new TarArchiveEntry(file, name);

        /** Step: 4 ---> Put the tar entry using putArchiveEntry. **/

        output.putArchiveEntry(entry);

        /**
         * Step: 5 ---> Write the data to the tar file and close the
         * input stream.
         **/

        FileInputStream fi = new FileInputStream(file);
        BufferedInputStream sourceStream = new BufferedInputStream(fi,  BUFFER);
        int count;
        byte data[] = new byte[BUFFER];
        while ((count = sourceStream.read(data, 0, BUFFER)) != -1) {
        	output.write(data, 0, count);
        }
        sourceStream.close();

        /** Step: 6 --->close the archive entry. **/

        output.closeArchiveEntry();
        
        
                
        numberOfFiles++;
        this.listener.setNumberOfEncrypted(numberOfFiles);
    }
    
    
   
    private void abrirArchivoSimple() throws Exception {
        try {
            out = new FileOutputStream(toEncrypt);
        } catch (Exception ex) {
            try {
                FileUtils.delete(new File(toEncrypt));
            } catch (Exception e) {
            }
            String[] lparam = {toEncrypt};
       	    throw new LockException(LockException.error_open, lparam);
        }
    }

    

    private void finalizarArchivos() throws Exception {
        boolean error = false;
        String mensaje = null;
        String files = null;

        for (File f : this.archivosIn) {

            try {
            
                    FileUtils.delete(f);
     

            } catch (Exception ex) {
                error = true;
                mensaje = ex.getMessage();
                files = files +", "+f.getAbsolutePath();
            }

        }

        if (error) {

            String[] lparam = {files};
            throw new LockException(LockException.error_delete2, lparam);
        }


    }

    
    

    private void generateFileListForFolder(File baseFolder, File node, List fileList) {
        if (node.isFile()) {
            fileList.add(generateZipEntry(baseFolder, node.getAbsoluteFile().toString()));
        }

        if (node.isDirectory()) {
            String[] subNote = node.list();
            for (String filename : subNote) {
            	generateFileListForFolder(baseFolder, new File(node, filename), fileList);
            }
        }

    }

    private String generateZipEntry(File baseF, String file) {
        int x = baseF.getParent().length() + 1;
        int y = file.length();
        return file.substring(x, y);
    }
    
    
    
	
	
	
	
	

}
