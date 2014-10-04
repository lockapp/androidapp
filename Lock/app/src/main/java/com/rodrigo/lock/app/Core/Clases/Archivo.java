package com.rodrigo.lock.app.Core.Clases;

import com.rodrigo.lock.app.Core.Utils.Utils;

import java.io.File;

/**
 * Created by Rodrigo on 28/09/2014.
 */
public class Archivo {

    File file;
    FileType tipo;

    public Archivo(File file) {
        this.file = file;

        String extension = Utils.getExtensionFile(file.getName());
        if(isEncExtension(extension)){
            tipo = FileType.OpenPBX;
        } else if (isExtensionImage(extension)){
            tipo = FileType.Imagen;
        } else if (isExtensionVideo(extension)){
            tipo = FileType.Video;
        } else {
            tipo = FileType.Otro;
        }

    }

    public File getFile() {
        return file;
    }

    public FileType getTipo() {
        return tipo;
    }


    public boolean soyMultimedia(){
        return  ( tipo ==FileType.Imagen || tipo == FileType.Video);
    }


    public static boolean isExtensionImage(String extension) {
        return (extension.equals("jpg") || extension.equals("jpeg") || extension.equals("jpe") || extension.equals("bmp") || extension.equals("webp")
                || extension.equals("png")
                || extension.equals("gif"));
    }

    public static boolean isExtensionVideo(String extension) {
        return (extension.equals("3gp") || extension.equals("mp4") || extension.equals("ts") || extension.equals("webm") || extension.equals("mkv") );
    }




    public  static boolean isEncExtension(String extension) {
        return  extension.equals("pbx");
    }

}
