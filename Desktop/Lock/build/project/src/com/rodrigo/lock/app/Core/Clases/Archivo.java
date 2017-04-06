package com.rodrigo.lock.app.Core.Clases;


import com.rodrigo.lock.app.Core.Utils.FileUtils;

import java.io.File;

/**
 * Created by Rodrigo on 28/09/2014.
 */
public class Archivo {

    File file;
    FileType tipo;

    public Archivo(File f) {
        this.file = f;

        String extension = FileUtils.getExtensionFile(file.getName());
        if (f.isDirectory()){
        	tipo = FileType.Carpeta;
        }else if(FileUtils.isEncExtension(extension)){
            tipo = FileType.OpenPBX;
        } else if (FileUtils.isExtensionImage(extension)){
            tipo = FileType.Imagen;
        } else if (FileUtils.isExtensionVideo(extension)){
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



}
