package com.rodrigo.lock.app.data.converters;

import com.rodrigo.lock.app.data.Clases.ImageToVault;
import com.rodrigo.lock.app.data.Clases.VideoToVault;
import com.rodrigo.lock.app.utils.MediaUtils;
import com.rodrigo.lock.core.datatype.DataNewFile;
import com.rodrigo.lock.core.datatype.INewFile;
import com.rodrigo.lock.core.utils.FileUtils;

import java.io.File;

/**
 * Created by Rodrigo on 07/12/2016.
 */

public class FileConverter {

    public static INewFile getFileToVault(File file){
        INewFile res = null;
        String extendion = FileUtils.getExtensionFile(file.getName());
        if (MediaUtils.isExtensionImage(extendion)){
            res = new ImageToVault(file);
        }else if (MediaUtils.isExtensionVideo(extendion)){
            res = new VideoToVault(file);
        }else{
            res = new DataNewFile(file);
        }
        return res;
    }
}
