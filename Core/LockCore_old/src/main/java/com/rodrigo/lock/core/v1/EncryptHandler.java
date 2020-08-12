package com.rodrigo.lock.core.v1;

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
import com.rodrigo.lock.core.clases.EncryptListener;
import com.rodrigo.lock.core.crypto.AES.CoreCrypto;
import com.rodrigo.lock.core.crypto.AES.CoreCryptoV0;
import com.rodrigo.lock.core.crypto.AES.CoreCryptoV1;
import com.rodrigo.lock.core.crypto.AES.Crypto;
import com.rodrigo.lock.core.crypto.AES.CoreCryptoV0.AES;
import com.rodrigo.lock.core.enums.CryptoAction;
import com.rodrigo.lock.core.v1.clases.EncryptOptions;

import exceptions.LockException;

public class EncryptHandler extends CryptoHandler{
	private EncryptOptions opciones;
	private String toEncrypt;// outFS;
	private EncryptListener listener;
	protected List<File> archivosIn;
	
	public EncryptHandler(List<File> archivosIn) {
		super( CryptoAction.TO_ENCRYPT);
		this.archivosIn = archivosIn;
	}
	
	public void init(EncryptOptions opciones ){
		this.opciones = opciones;
		
		 for (File inF: this.archivosIn){
             if (!inF.exists()) {
            	 String[] lparam = {inF.getName()};
            	 throw new LockException(LockException.file_not_found_2, lparam);
             }
         }

         //se pone el nombre del primero            
         String name  = opciones.getOutFileName();
         String path = opciones.getPathToSave();
         toEncrypt = path + File.separator + name +"."+FileUtils.ENC_EXTENSION;

         if ((new File(toEncrypt)).exists()) {
        	 toEncrypt = FileUtils.createNewFileNameInPath(path + File.separator, name, FileUtils.ENC_EXTENSION);
         }		
	}
		
	
	private String pass;
	private ZipOutputStream output = null;
	private OutputStream out = null;
	
	
	public void encrypt(EncryptListener listener) throws Exception {
		this.listener = listener;
        abrirArchivoSimple();
        grabarCabezales();

        if ( this.opciones.getSoloAbrirEnEsteDispositivo()) {
        	pass = opciones.mergeIdInPassword(this.opciones.getPassword());
        }else{
        	pass = opciones.getPassword();        	
        }
                
        CoreCrypto algo = Crypto.createCryptoV1();   
        out = new CipherOutputStream(out,  algo.getCiphertoEncZip(out, pass));
        
        output = new ZipOutputStream(new BufferedOutputStream(out));
        output.setLevel(Deflater.NO_COMPRESSION);

        grabarListaArchivos();

        if (!opciones.getDejarCopiaSinBloquear()){
            finalizarArchivos();
        }

    }
	

	private void grabarCabezales() throws Exception {
        try {
         /*
            -1 byte version 00000000
            -1 byte cabezales activos
                version1: 00000(vistasegura)(solo aca)(caducidad)
                version2: 000(cifrar)(prhoibirextraer) (vistasegura )(solo aca)(caducidad)

            -en cado de cabezal caducidad: 4byte para la fecha que es un int en formato aaaammdd

            */
            int tamcabezalCompleto = 2;

            byte[] version = new byte[1];
            Arrays.fill(version, Byte.parseByte("00000001", 2));

            byte[] cavezalesActivos = new byte[1];
            String activos = "000";

            //cifrar
            activos =   activos + "1" ;
            
            //prhoibir extraer
            //activos = ((opciones.getProhibidoExtraer()) ?   activos + "1" : activos + "0");
            activos=activos + "0";
            
            //vista segura
            activos = ( activos + "0");
            //solo aca
            activos = ((opciones.getSoloAbrirEnEsteDispositivo()) ?   activos + "1" : activos + "0");
            //caducidad
            if (opciones.getFechaVencimiento() != null) {
                activos = activos + "1";
                tamcabezalCompleto = tamcabezalCompleto + 4;
            } else {
                activos = activos + "0";
            }
            Arrays.fill(cavezalesActivos, Byte.parseByte(activos, 2));


            //se graba el resto cabezal
            byte[] cabezalCompleto = new byte[tamcabezalCompleto];
            cabezalCompleto[0] = version[0];
            cabezalCompleto[1] = cavezalesActivos[0];

            if (opciones.getFechaVencimiento() != null) {
                byte[] caducidad = FileUtils.intToByteArray(DateUtils.convertFechaCaducidadToInt(opciones.getFechaVencimiento()));
                for (int i = 0; i < caducidad.length; i++) {
                    cabezalCompleto[i + 2] = caducidad[i];
                }
            }

            out.write(cabezalCompleto, 0, cabezalCompleto.length);
        } catch (Exception ex) {
            try {
                //no se a a;adido ala galeria basta elimnar asi
                FileUtils.delete(new File(toEncrypt));
            } catch (Exception e) {
            }

       	 	String[] lparam = {toEncrypt};
            throw new LockException(LockException.error_open, lparam);
        }
    }


    private void grabarListaArchivos() throws Exception {
        try {
            byte[] buffer = new byte[1024];
            for (File file : this.archivosIn) {
            	
            	if (file.isDirectory()){
            		List <String> fileList = new LinkedList<String> ();
            		generateFileListForFolder(file, file, fileList); 
            		
            		for (String hijo : fileList) {
            			grabarOutput(hijo, new FileInputStream(file.getParent() + File.separator + hijo), buffer);
            		}
            		
            	}else{
            		grabarOutput(file.getName(),  new FileInputStream(file), buffer);
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
            output.closeEntry();
            output.close();


        } catch (Exception ex) {
            try {
                if (output != null) {
                    output.closeEntry();
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
    private void grabarOutput(String name, FileInputStream inf, byte[] buffer) throws Exception{    	
        //System.out.println("File Added : " + file);
        ZipEntry ze = new ZipEntry(name);        
        output.putNextEntry(ze);
        
        //BufferedOutputStream out = new BufferedOutputStream(output);
        BufferedInputStream in = new BufferedInputStream(inf);

        int len;
        while ((len = in.read(buffer,0,1024)) >= 0) {
        	output.write(buffer, 0, len);
           // avanza = avanza + len;
        }
        inf.close();
        
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
