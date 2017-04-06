package com.rodrigo.lock.app.Core.controllers.crypto;


import com.rodrigo.lock.app.Core.Clases.Accion;
import com.rodrigo.lock.app.Core.Clases.Archivo;
import com.rodrigo.lock.app.Core.Clases.FileType;
import com.rodrigo.lock.app.Core.Utils.FileUtils;
import com.rodrigo.lock.app.Core.controllers.FileController;
import com.rodrigo.lock.app.Core.crypto.AES.Crypto;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javafx.scene.image.Image;

import javax.crypto.CipherOutputStream;

/**
 * Created by Rodrigo on 25/05/14.
 */

/*
     El inicio es distinto dependisndo del tipo de archivo despues
    -1 byte version 00000000
    -1 byte cabezales activos
        version1: 00000(vistasegura)(solo aca)(caducidad)
        version2: 000(cifrar)(prhoibirextraer) (vistasegura )(solo aca)(caducidad)

    -en caso de cabezal caducidad: 4byte para la fecha que es un int en formato aaaammdd

*/

public class EncryptController extends CryptoController {

    LinkedList<Archivo> inFileList;
    String toEncrypt;

    public String getToEncrypt() {
        return toEncrypt;
    }

    public EncryptController(FileController fc) {
        this.toEncrypt = fc.getOutFS();
        this.pass = fc.getPassword();
        this.cabezal = fc.getCabezal();
        this.inFileList = fc.getInFiles();
        this.accion =fc.getAccion();
        this.name=fc.getName();
    }

    OutputStream out = null;
    ZipOutputStream output = null;
    Crypto algo = null;

    @Override
    public void realizarTrabajo()  throws Exception{
        encrypt();
    }



    private void encrypt() throws Exception {
    	/*
        if (Accion.EncryptarConImagen == accion) {
            abrirArchivoImagen();
        } else {
           abrirArchivoSimple(); 
        }*/
        
        abrirArchivoSimple();

        grabarCabezales();

        if (cabezal.isCifrar()){
            if ( cabezal.isSoloAca()) {
                pass = cabezal.mergeIdInPassword(pass);
            }
            algo = new Crypto();
            algo.initV1();
            out = new CipherOutputStream(out, algo.getCiphertoEnc(out, pass));
        }
        output = new ZipOutputStream(new BufferedOutputStream(out));
        output.setLevel(Deflater.NO_COMPRESSION);

        grabarListaArchivos();

        if (!cabezal.isCopiaSinBloquear()){
            finalizarArchivos();
        }

        



    }


    public void grabarCabezales() throws Exception {
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
            activos =( (cabezal.isCifrar()) ?   activos + "1" : activos + "0");
            //prhoibir extraer
            activos = ((cabezal.isProhibirExtraer()) ?   activos + "1" : activos + "0");
            //vista segura
            activos = ((accion == Accion.EncryptarConImagen) ?   activos + "1" : activos + "0");
            //solo aca
            activos = ((cabezal != null && cabezal.isSoloAca()) ?   activos + "1" : activos + "0");
            //caducidad
            if (cabezal != null && cabezal.isCaducidad()) {
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

            if (cabezal != null && cabezal.isCaducidad()) {
                byte[] caducidad = FileUtils.intToByteArray(cabezal.getFechaCaducidad());
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

            throw new Exception(String.format(bundle.getString("error_open"), toEncrypt, ex.getMessage()));
        }
    }


    private void grabarListaArchivos() throws Exception {
        try {

            double avanza = 0;
            byte[] buffer = new byte[1024];
            for (Archivo file : this.inFileList) {
            	
            	if (file.getTipo() == FileType.Carpeta){
            		
            		List <String> fileList = new LinkedList<String> ();
            		generateFileListForFolder(file.getFile(), file.getFile(), fileList); 
            		
            		for (String hijo : fileList) {
            			grabarOutput(hijo, new FileInputStream(file.getFile().getParent() + File.separator + hijo), buffer);
            		}
            		
            	}else{
            		grabarOutput(file.getFile().getName(),  new FileInputStream(file.getFile()), buffer);
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
            throw new Exception(String.format(bundle.getString("error_lock2"), ex.getMessage()));
        }
    }


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
    }
    
    
    
    
    private void abrirArchivoImagen() throws Exception {
        try {
            //formato
            //-0xFF, 0xD8, start of image
            // -image to show
            // -0xFF, 0xD9, End Of Image
            // -pandorabox = 0x50, 0x41, 0x4E, 0x44, 0x4F, 0x52, 0x41, 0x42, 0x4F, 0x58
            //
            //despues sigue la version con los cabezales

            //se copia la imagen al nuevo archivo
 
        	File slpash = new File("file:resources/images/imagenbloqueada.jpg");
            InputStream in = new FileInputStream(slpash);
            
            out = new FileOutputStream(toEncrypt);

            //secopia el splash
            //imagenbloqueada empieza con 0xff, 0xd8
            //y termina con 0xFF, 0xD9 por lo que no hay que chequear nada...
            byte[] buf = new byte[1024];
            int numRead = 0;
            while ((numRead = in.read(buf)) >= 0) {
                out.write(buf, 0, numRead);
            }

            //se graba el nombre del programa para identifical la imagen
            out.write(FileUtils.PANDORABOX, 0, FileUtils.PANDORABOX.length);


        } catch (Exception ex) {
            try {
                FileUtils.delete(new File(toEncrypt));
            } catch (Exception e) {
            }

            throw new Exception(String.format(bundle.getString("error_open"), toEncrypt, ex.getMessage()));
        }
    }


    private void abrirArchivoSimple() throws Exception {
        try {
            out = new FileOutputStream(toEncrypt);

        } catch (Exception ex) {
            try {
                FileUtils.delete(new File(toEncrypt));
            } catch (Exception e) {
            }

            throw new Exception(String.format(bundle.getString("error_open"), toEncrypt, ex.getMessage()));
        }
    }

    

    public void finalizarArchivos() throws Exception {
        boolean error = false;
        String mensaje = null;
        String files = null;

        for (Archivo f : this.inFileList) {

            try {
                    FileUtils.delete(f.getFile());
     

            } catch (Exception ex) {
                error = true;
                mensaje = ex.getMessage();
                files = files +", "+f.getFile().getAbsolutePath();
            }

        }

        if (error) {
            throw new Exception(String.format(bundle.getString("error_delete2"),files,  mensaje));
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
