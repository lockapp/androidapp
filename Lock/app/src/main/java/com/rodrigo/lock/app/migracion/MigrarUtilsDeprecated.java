package com.rodrigo.lock.app.migracion;

import android.content.Context;

import com.rodrigo.lock.app.LockApplication;
import com.rodrigo.lock.app.old.Core.Utils.FileUtils;
import com.rodrigo.lock.app.old.Core.Utils.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.Serializable;

/**
 * Created by Rodrigo on 03/12/2016.
 */

public class MigrarUtilsDeprecated implements Serializable {
    public static final String ENC_EXTENSION = "pbx";

    private int offset;
    private String fullpath;

    public MigrarUtilsDeprecated(){
    }

    public MigrarUtilsDeprecated(String fullpath)  {
        this.fullpath = fullpath;
    }

    public boolean esEncriptadoViejo() {
        try{
            File archivo = new File(fullpath);
            String extension = com.rodrigo.lock.core.utils.FileUtils.getExtensionFile(new File(fullpath).getName());
            //String extension = FileUtils.getExtensionFile(archivo.getName());
            if (isEncExtension(extension)){
                return true;
            }else if (isExtensionImage(extension)){
                offset = isImageEncrypted(archivo);
                //esta contenido en una imagen
                if (offset > 0) {
                    return true;
                }
            }
        }catch (Exception e){
        }
        return false;
    }


    public static boolean isExtensionImage(String extension) {
        return (extension.equals("jpg") || extension.equals("jpeg") || extension.equals("jpe") || extension.equals("bmp") || extension.equals("webp")
                || extension.equals("png")
                || extension.equals("gif"));
    }


    public  static boolean isEncExtension(String extension) {
        return  extension.equals(ENC_EXTENSION);
    }


    private static int isImageEncrypted(File f) throws Exception {
        Context contex =LockApplication.getAppContext();
        //byte[] PANDORABOX = FileUtils.PANDORABOX;
        long size = contex.getAssets().openFd("imagenbloqueada.jpg").getLength();

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




    public static String mergeIdInPassword(String password){
        String android_id = Utils.getUniquePsuedoID();
        return password + android_id;
       /* String newPassword ="";
        int passiter =0;
        int iditer=0;

        while (passiter < password.length() && iditer<android_id.length()){
            newPassword = newPassword + password.charAt(passiter) +android_id.charAt(iditer);
            passiter++;
            iditer++;
        }

        while (passiter < password.length()){
            newPassword = newPassword + password.charAt(passiter);
            passiter++;
        }

        while ( iditer<android_id.length()){
            newPassword = newPassword  +android_id.charAt(iditer);
            iditer++;
        }
        return newPassword;*/

    }


    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public String getFullpath() {
        return fullpath;
    }

    public void setFullpath(String fullpath) {
        this.fullpath = fullpath;
    }
}
