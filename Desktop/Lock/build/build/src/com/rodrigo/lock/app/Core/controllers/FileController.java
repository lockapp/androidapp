package com.rodrigo.lock.app.Core.controllers;


import com.rodrigo.lock.app.Core.Clases.Accion;
import com.rodrigo.lock.app.Core.Clases.Archivo;
import com.rodrigo.lock.app.Core.Clases.FileHeader;
import com.rodrigo.lock.app.Core.Clases.FileType;
import com.rodrigo.lock.app.Core.Utils.FileUtils;
import com.rodrigo.lock.app.Core.controllers.crypto.CryptoController;
import com.rodrigo.lock.app.Core.controllers.crypto.DecryptController;
import com.rodrigo.lock.app.Core.controllers.crypto.EncryptController;
import com.rodrigo.lock.app.Utils.LenguajeUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ResourceBundle;


/**
 * Created by Rodrigo on 25/05/14.
 */
public class FileController  {
    Accion accion;
    LinkedList<Archivo> inFileList;
    ResourceBundle bundle;

  //  CryptoController cryptoController;

    private FileHeader cabezal = null;
    private String outFS;
    private String password;

    private int offset;
    private int id;

    public FileController(int id)  {
        inFileList = new LinkedList<Archivo>();
        this.id = id;
    	bundle = LenguajeUtils.getBundle();
    }
/*
    public CryptoController getCryptoController() {
        return cryptoController;
    }

    public void setCryptoController(CryptoController cryptoController) {
        this.cryptoController = cryptoController;
    }
*/
    public int getId() {
        return id;
    }

    public void setPassword(String pass){
        this.password =pass;
    }

    public Accion getAccion() {
        return this.accion;
    }

    public LinkedList<Archivo> getInFiles() {
        return this.inFileList;
    }

    public String getPassword(){
        return password;
    }

    public void addFile(Archivo f){
        inFileList.add(f);
    }

    public String getOutFS() {
        return outFS;
    }

    public int getOffset() {
        return offset;
    }

    public FileHeader getCabezal() {
        return cabezal;
    }

    public void setCabezal(FileHeader c) {
        cabezal=c;
    }


    public void resolverAccion()throws Exception{
        //this.vinoComo = vinoComo;
        //si vinieron como media se a;ade una imagen a la galaeria
        if(inFileList.size()==0) {
            throw new Exception(bundle.getString("error_notfound"));
        }else if (inFileList.size() == 1){
            accion = getActionForFile(inFileList.getFirst());
        }else {
            accion = Accion.Encyptar;
         
        }

    }


    private Accion getActionForFile(Archivo inF) throws Exception {

        //es cualquier archivo
        if (inF.getTipo() == FileType.OpenPBX) {
            return Accion.Desencryptar;

            //es imagen
        } else if (inF.getTipo() == FileType.Imagen) {
            offset = isImageEncrypted(inF.getFile());
            //esta contenido en una imagen
            if (offset > 0) {
                return Accion.DesencryptarConImagen;
            } 
        } 
    
        return Accion.Encyptar;
        
    


    }

//metodo para ver si es una imagen encryptada que sirve con cualquier imagen en el inicio
 /*   private int isImageEncrypted(File f) throws Exception {
        InputStream in = new FileInputStream(f);
        byte[] bFile = new byte[(int) f.length()];

        in.read(bFile);
        in.close();


        int base = 0;
        while (base < bFile.length) {

            if (((byte) 0xFF) == bFile[base]) {

                if (((byte) 0xD9) == bFile[base + 1]) {
                    base = base + 2;
                    byte[] PANDORABOX = Utils.getPANDORABOX();

                    int iter = 0;
                    boolean ret = true;

                    while (ret && (iter < PANDORABOX.length) && ((base + iter) < bFile.length)) {
                        ret = (PANDORABOX[iter] == bFile[base + iter]);
                        iter++;
                    }

                    if (ret && (iter == PANDORABOX.length))
                        return base + iter;
                    else
                        return -1;


                } else if (!((((byte) 0x00) == bFile[base + 1]) || (((byte) 0x01) == bFile[base + 1]) ||
                        ((((byte) 0xD0) <= bFile[base + 1]) && (((byte) 0xD8) >= bFile[base + 1])))) {
                    byte[] temp = new byte[2];
                    int result = ((bFile[base + 3] << 8) & 0x0000ff00) | (bFile[base + 2] & 0x000000ff);
                    base = base + result + 3;
                }
            }
            base++;
        }
        return -1;
    }
*/


    private int isImageEncrypted(File f) throws Exception {
        //byte[] PANDORABOX = FileUtils.PANDORABOX;
    	File slpash = new File("file:resources/images/imagenbloqueada.jpg");
        long size = slpash.length();

        if (size + FileUtils.PANDORABOX.length > f.length())
            return -1;

        InputStream in = new FileInputStream(f);
        in.skip(size);

        byte[] bFile = new byte[FileUtils.PANDORABOX.length];
        int leido = 0;
        while (leido<FileUtils.PANDORABOX.length){
            leido+=in.read(bFile, leido, FileUtils.PANDORABOX.length-leido);
        }
        in.close();

        int iter = 0;
        boolean ret = true;

        while ( iter < FileUtils.PANDORABOX.length ) {
            if  (FileUtils.PANDORABOX[iter] != bFile[ iter])
                return  -1;
            iter++;
        }

        return  (iter + (int)size);
    }



/*
    public long getSizesOfFilesinB(){
        long size =0;

        for (Archivo f : this.inFileList){
            size += getSize(f.getFile());
        }
        return (size);

    }

    private long getSize(File directory) {
        long length = 0;
        if (!directory.isDirectory()) {
            length = directory.length();
        } else {

            for (File file : directory.listFiles()) {
                if (file.isFile()) {
                    length += file.length();
                } else {
                    length += getSize(file);
                }
            }
        }

        return length;

    }


    public String getSizesOfFiles(){
        if (size == null)
            size = transformSize(getSizesOfFilesinB());

        return size;
    }


    private String transformSize(long aux ) {
        DecimalFormat formatter = new DecimalFormat("#########.##");

        if (aux < 1024){
            return String.valueOf(aux) + " B";
        }

        aux = aux / 1024;
        if ( aux <1024) {
            return String.valueOf(formatter.format(aux)) + " kB";
        }

        aux = aux / 1024;
        if (aux <1024) {
            return String.valueOf(formatter.format(aux)) + " MB";
        }

        aux = aux / 1024;
        return String.valueOf(formatter.format(aux)) + " GB";

    }

*/
    
    String name=null;
    String size=null;

    public String getName(){
    	if (LenguajeUtils.isEmpty(name)){
    		initNameAndSize();
    	}    	
    	return name;
    }
    
    public String getSize(){
    	if (LenguajeUtils.isEmpty(size)){
    		initNameAndSize();
    	}    	
    	return size;
    }

    
    private void initNameAndSize(){
    	int count = 0;   
    	long size = 0;
    	for (Archivo a : inFileList){
    		if (a.getTipo() == FileType.Carpeta){
    			count += a.getFile().listFiles().length;
    			size += folderSize(a.getFile());
    		}else{
    			count ++;
    			size += a.getFile().length();
    		}
    	}
    	
        if(count == 1){
            name= (bundle.getString("file"));
        }else{
            name = ( count + " " + bundle.getString("files"));
        }
        this.size = getSize(size);
    }
    


    private String getSize(long tamF) {
        long aux = tamF;
        DecimalFormat formatter = new DecimalFormat("#########.##");
        String fileSize = String.valueOf(tamF) + " B";
        aux = aux / 1024;

        if (aux > 1) {
            fileSize = String.valueOf(formatter.format(aux)) + " kB";
        }
        aux = aux / 1024;
        if (aux > 1) {
            fileSize = String.valueOf(formatter.format(aux)) + " MB";
        }
        aux = aux / 1024;
        if (aux > 1) {
            fileSize = String.valueOf(formatter.format(aux)) + " GB";
        }
        return fileSize;
    }
    
    
    
    
    
    public static long folderSize(File directory) {
        long length = 0;
        for (File file : directory.listFiles()) {
            if (file.isFile())
                length += file.length();
            else
                length += folderSize(file);
        }
        return length;
    }
    
    
    
    
    

    public boolean chequear() throws Exception {
        if (accion == Accion.EncryptarConImagen || accion == Accion.Encyptar) {
            for (Archivo inF: inFileList){
                if (!inF.getFile().exists()) {
                    throw new Exception(String.format(bundle.getString("error_notfound2"), inF.getFile().getName()));
                }
            }

            //se pone el nombre del primero            
            String extension;
            String name = "lockFile";//inF.getName().replace(".", "");

            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hhmm ");
            String actualDate = df.format(c.getTime());
            name= actualDate + name;

            if (accion == Accion.EncryptarConImagen) {
                extension = ".jpg";
            }else {
                extension = "."+FileUtils.ENC_EXTENSION;
            }



            File inF= inFileList.getFirst().getFile();
            outFS = inF.getParent() + File.separator + name + extension;

            if ((new File(outFS)).exists()) {
                outFS = FileUtils.createNewFileNameInPath(inF.getParent() + File.separator, name, extension);
            }



        } else if (accion == Accion.DesencryptarConImagen || accion == Accion.Desencryptar) {
            File inF= inFileList.getFirst().getFile();
            if (!inF.exists()) {
                throw new Exception(bundle.getString("error_notfound"));
            }
            
            outFS =  inF.getParent();
           


        }


        return true;
    }



    /////////////////////
    public CryptoController getDecryptController() throws Exception {
        DecryptController d =new DecryptController(getInFiles().getFirst().getFile() ,getPassword(),getAccion(),getOffset(), getName(), cabezal.isCopiaSinBloquear());
        return d;
    }




    public CryptoController getEncryptController() throws Exception {
        EncryptController d =new EncryptController(this);
        return d;
    }



}
