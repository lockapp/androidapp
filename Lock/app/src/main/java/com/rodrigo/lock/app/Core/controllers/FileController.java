package com.rodrigo.lock.app.Core.controllers;

import android.content.Context;

import com.rodrigo.lock.app.Core.Clases.Accion;
import com.rodrigo.lock.app.Core.Clases.Archivo;
import com.rodrigo.lock.app.Core.Clases.FileHeader;
import com.rodrigo.lock.app.Core.Clases.FileType;
import com.rodrigo.lock.app.Core.Utils.Utils;
import com.rodrigo.lock.app.Core.controllers.crypto.CryptoController;
import com.rodrigo.lock.app.Core.controllers.crypto.DecryptController;
import com.rodrigo.lock.app.Core.controllers.crypto.DecryptControllerSeeMedia;
import com.rodrigo.lock.app.Core.controllers.crypto.EncryptController;
import com.rodrigo.lock.app.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.LinkedList;


/**
 * Created by Rodrigo on 25/05/14.
 */
public class FileController  {
    Accion accion;
    LinkedList<Archivo> inFileList;
  //  CryptoController cryptoController;

    private FileHeader cabezal = null;
    private String outFS;
    private String password;
    private String size;
    private Context contex;

    private int offset;
    private int id;

    public FileController(int id, Context contex)  {
        inFileList = new LinkedList<Archivo>();
        this.id = id;
        this.contex = contex;
        this.size=null;
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
            throw new Exception(this.contex.getResources().getString(R.string.error_notfound));
        }else if (inFileList.size() == 1){
            accion = getActionForFile(inFileList.getFirst());
        }else {
            boolean sonMultimedia = true;
            Iterator i = inFileList.iterator();
            while (sonMultimedia && i.hasNext()){
                Archivo a = (Archivo)i.next();
                sonMultimedia = sonMultimedia && (a.soyMultimedia());
            }

            if(sonMultimedia){
                accion= Accion.EncryptarConImagen;
            }else {
                accion = Accion.Encyptar;
            }


        }

    }


    public Accion getActionForFile(Archivo inF) throws Exception {

        //es cualquier archivo
        if (inF.getTipo() == FileType.OpenPBX) {
            return Accion.Desencryptar;

            //es imagen
        } else if (inF.getTipo() == FileType.Imagen) {
            offset = isImageEncrypted(inF.getFile());
            //esta contenido en una imagen
            if (offset > 0) {
                return Accion.DesencryptarConImagen;
            } else {
                return Accion.EncryptarConImagen;
            }

        } else if (inF.getTipo() == FileType.Video) {
            return Accion.EncryptarConImagen;

        }else{
            return Accion.Encyptar;
        }


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
        byte[] PANDORABOX = Utils.getPANDORABOX();
        long size = contex.getAssets().openFd("imagenbloqueada.jpg").getLength();

        if (size + PANDORABOX.length > f.length())
            return -1;

        InputStream in = new FileInputStream(f);
        in.skip(size);

        byte[] bFile = new byte[PANDORABOX.length];
        int leido = 0;
        while (leido<PANDORABOX.length){
            leido+=in.read(bFile, leido, PANDORABOX.length-leido);
        }
        in.close();

        int iter = 0;
        boolean ret = true;

        while ( iter < PANDORABOX.length ) {
            if  (PANDORABOX[iter] != bFile[ iter])
                return  -1;
            iter++;
        }

        return  (iter + (int)size);
    }




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



    public String getName(){
        if(this.inFileList.size() == 1){
            return inFileList.getFirst().getFile().getName();
        }else{
            return (String.format(contex.getResources().getString(R.string.files), inFileList.size() ));
        }
    }




    public boolean chequear() throws Exception {
        if (accion == Accion.EncryptarConImagen || accion == Accion.Encyptar) {
            for (Archivo inF: inFileList){
                if (!inF.getFile().exists()) {
                    throw new Exception(String.format(this.contex.getResources().getString(R.string.error_notfound2), inF.getFile().getName()));
                }
            }
            //se pone el nombre del primero
            File inF= inFileList.getFirst().getFile();
            String extension;
            String name = inF.getName().replace(".", "");

            if (accion == Accion.EncryptarConImagen) {
                extension = ".jpg";
            }else {
                extension = "."+Utils.getEncExtension();
            }

            outFS = inF.getParent() + File.separator + name + extension;

            if ((new File(outFS)).exists()) {
                outFS = Utils.getPathFileNoExists(inF.getParent() + File.separator ,name,extension);
            }



        } else if (accion == Accion.DesencryptarConImagen || accion == Accion.Desencryptar) {
            File inF= inFileList.getFirst().getFile();
            if (!inF.exists()) {
                throw new Exception(this.contex.getResources().getString(R.string.error_notfound));
            }


        }


        return true;
    }



    /////////////////////
    public CryptoController getDecryptController(Context appContext) throws Exception {
        DecryptController d =new DecryptController(appContext,getInFiles().getFirst().getFile() ,getPassword(),getAccion(),getOffset()



        );
        return d;
    }



    public CryptoController getDecryptControllerSeeMedia(Context appContext) throws Exception {
        DecryptControllerSeeMedia d =new DecryptControllerSeeMedia (getId(), appContext,getInFiles().getFirst().getFile() ,getPassword(),getAccion(),getOffset());
        return d;
    }

    public CryptoController getEncryptController() throws Exception {
        EncryptController d =new EncryptController(this);
        return d;
    }



}
