package com.rodrigo.lock.app.data.converters;

import com.rodrigo.lock.app.data.Clases.Vault;
import com.rodrigo.lock.app.data.Clases.VaultContent;
import com.rodrigo.lock.app.utils.MediaUtils;
import com.rodrigo.lock.core.clases.LockFile;
import com.rodrigo.lock.core.utils.FileUtils;

import java.io.File;

/**
 * Created by Rodrigo on 19/11/2016.
 */

public class Converter {

    public static Vault convertVault(File f){
        Vault v=new Vault();
        v.setFullPath(f.getAbsolutePath());
        v.setName(FileUtils.removeExtensionFile(f.getName()));
        v.setSize(FileUtils.sizeToString(FileUtils.getSize(f)));
        return v;
    }


    public static VaultContent convertVaultContent(LockFile lockFile){
        VaultContent content = new VaultContent();
        content.setFullPath(lockFile.getFullPath());
        content.setExtrayendo(false);
        content.setId(lockFile.getId());
        content.setSize(FileUtils.sizeToString(lockFile.getSize()));
        content.setType(lockFile.getType());
        String extension = FileUtils.getExtensionFile(lockFile.getFullPath());
        content.setEsVideo(MediaUtils.isExtensionVideo(extension));
        return content;
    }
}
