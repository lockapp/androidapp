package com.rodrigo.lock.app.data.source;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.text.TextUtils;

import com.rodrigo.lock.app.Constants;
import com.rodrigo.lock.app.LockApplication;
import com.rodrigo.lock.app.data.Clases.ImageToVault;
import com.rodrigo.lock.app.data.Clases.VaultContent;
import com.rodrigo.lock.app.data.Clases.VideoToVault;
import com.rodrigo.lock.app.data.converters.Converter;
import com.rodrigo.lock.app.utils.MediaUtils;
import com.rodrigo.lock.core.EncryptedFileSystem;
import com.rodrigo.lock.core.EncryptedFileSystemHandler;
import com.rodrigo.lock.core.clases.FileType;
import com.rodrigo.lock.core.clases.LockFile;
import com.rodrigo.lock.core.utils.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;

import rx.Observable;
import rx.functions.Func1;

/**
 * Created by Rodrigo on 11/12/2016.
 */

public class ContentVaultRepository {
    EncryptedFileSystem fileSystem;
    String fileSystemPath;
    String cachePath;
    //String extractPath;


    private ContentVaultRepository(String path, String password) throws IOException {
        fileSystemPath = path;
        fileSystem = EncryptedFileSystemHandler.openEncryptedFile(path, password);
        String vaultName = FileUtils.removeExtensionFile((new File(path)).getName());
        cachePath = Constants.Storage.DEFAULT_OPEN_VAULT_DIRECTORY + File.separator + vaultName;
        //extractPath = Preferences.getDefaultUnlockDirectory() + File.separator + vaultName;
        clearCache();
    }




    public static Observable<ContentVaultRepository> openEncryptedFileSystem(final String path, final String password) {
        return Observable.fromCallable(new Callable<ContentVaultRepository>() {
            @Override
            public ContentVaultRepository call() throws Exception {
                return new ContentVaultRepository(path, password);
            }
        });

    }

    public Observable<List<VaultContent>> getFiles() {
        if (fileSystem == null) {
            return Observable.empty();
        }

        return Observable.from(fileSystem.getFilesAndFolders())
                .filter(new Func1<LockFile, Boolean>() {
                    @Override
                    public Boolean call(LockFile lockFile) {
                        if (lockFile == null) {
                            return false;
                        }
                        return (lockFile.getType() != FileType.FOLDER && lockFile.getType() != FileType.PREVIEW);
                    }
                })
                .map((new Func1<LockFile, VaultContent>() {
                    @Override
                    public VaultContent call(LockFile srcObj) {
                        return Converter.convertVaultContent(srcObj);
                    }
                }))
                .toList();

    }

    public void clearCache() throws IOException {
        File baseFolder = new File (cachePath);
        if (baseFolder.exists()) {
            FileUtils.delete(baseFolder);
        }
    }

    public Observable<Bitmap> getPreview(final String idFile){
        return Observable.fromCallable(new Callable<Bitmap>() {
            @Override
            public Bitmap call() throws Exception {
                String previewId =fileSystem.getPreviewIdOfFile(idFile);
                if (TextUtils.isEmpty(previewId)){
                    return null;
                }
                File cacheFile = extractFileInCache(previewId);
                return BitmapFactory.decodeFile(cacheFile.getAbsolutePath());
                //return cacheFile;
            }
        });
    }

    public Observable<Uri> getFile(final String idFile){
        return Observable.fromCallable(new Callable<Uri>() {
            @Override
            public Uri call() throws Exception {
                File f = extractFileInCache(idFile);
                return CacheAccessProvider.getUriForCacheFile(f);
            }
        });
    }
/*
    public Observable<File> extractFile(final String idFile){
        return Observable.fromCallable(new Callable<File>() {
            @Override
            public File call() throws Exception {
                return doExtractFile(idFile);
            }
        });
    }
*/
    private File extractFileInCache(String idFile) throws Exception{
        File file = new File(cachePath + File.separator + idFile);
        if (!file.exists()){
            new File(file.getParent()).mkdirs();
            fileSystem.extractFile(idFile, new FileOutputStream(file));
        }
        return file;
    }
/*
    private File doExtractFile(String idFile) throws Exception{
        LockFile lockFile = fileSystem.getFile(idFile);
        File file = new File(extractPath + File.separator + lockFile.getFullPath());
        String extension = FileUtils.getExtensionFile(file.getName());
        if (file.exists()){
            file = new File(FileUtils.createNewFileNameInPath(file.getParent() + File.separator,FileUtils.removeExtensionFile(file.getName()), extension));
        }else{
            new File(file.getParent()).mkdirs();
        }
        fileSystem.extractFile(lockFile.getId(), new FileOutputStream(file));
        if (MediaUtils.isExtensionImage(extension)){
            MediaUtils.addImageToGallery(file);
        }else if (MediaUtils.isExtensionVideo(extension)){
            MediaUtils.addVideoToGallery(file);
        }
        return file;
    }*/


    public Observable deleteFile(final String idFile){
        return Observable.fromCallable(new Callable() {
            @Override
            public Object call() throws Exception {
                LockFile lockFile = fileSystem.getFile(idFile);
                fileSystem.deleteFiles(idFile);
                LockApplication.requestForcedSync();
                return null;
            }
        });
    }



    public void removeFromUso(){
        EncryptedFileSystemHandler.removeFromUso(fileSystemPath);
    }

}
