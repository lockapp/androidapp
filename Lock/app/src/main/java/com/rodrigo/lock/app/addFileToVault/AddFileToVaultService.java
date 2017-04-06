package com.rodrigo.lock.app.addFileToVault;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.rodrigo.lock.app.bus.EventType;
import com.rodrigo.lock.app.bus.Event;
import com.rodrigo.lock.app.bus.EventBus;
import com.rodrigo.lock.app.R;
import com.rodrigo.lock.app.data.Clases.ImageToVault;
import com.rodrigo.lock.app.data.Clases.VideoToVault;
import com.rodrigo.lock.app.data.converters.FileConverter;
import com.rodrigo.lock.app.utils.MediaUtils;
import com.rodrigo.lock.core.EncryptedFileSystem;
import com.rodrigo.lock.core.EncryptedFileSystemHandler;
import com.rodrigo.lock.core.datatype.AddFileListener;
import com.rodrigo.lock.core.datatype.INewFile;
import com.rodrigo.lock.core.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Rodrigo on 29/05/14.
 */
public class AddFileToVaultService extends IntentService {
    public static final String EXTRA_VAULT_PATH = "EXTRA_VAULT_PATH";
    public static final String EXTRA_VAULT_PASSWORD = "EXTRA_VAULT_PASSWORD";
    public static final String EXTRA_ARCHIVOS = "EXTRA_ARCHIVOS";


    static int id = 0;
    NotificationManager mNotifyManager;
    NotificationCompat.Builder mBuilder;

    public AddFileToVaultService() {
        super("AddFileToVaultService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        id++;

        mNotifyManager = (NotificationManager) getApplication().getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentTitle(getResources().getString(R.string.aniadiendo_archivos));
        //mBuilder.setContentText(getResources().getString(R.string.workingU));
        mBuilder.setSmallIcon(R.drawable.ic_action_secure);
        mBuilder.setProgress(0, 0, true);
        mBuilder.setOngoing(true);
        startForeground(id, mBuilder.build());


        String path = intent.getExtras().getString(EXTRA_VAULT_PATH);
        String password = intent.getExtras().getString(EXTRA_VAULT_PASSWORD);
        ArrayList<String> archivos = intent.getStringArrayListExtra(EXTRA_ARCHIVOS);


        try {
            Event eventIniti = new Event();
            eventIniti.setEventType(EventType.EMPEZANDO_ANIADIR_ARCHIVOS);
            eventIniti.setVaultPath(path);
            EventBus.getInstance().addEvent(eventIniti);


            EncryptedFileSystem fileSystem = EncryptedFileSystemHandler.openEncryptedFile(path, password);


            List<INewFile> toadd = new LinkedList<>();
            for (String filePath : archivos) {
                toadd.add(FileConverter.getFileToVault(new File(filePath)));
            }
            fileSystem.addFilesWithPreview(new AddFileListener(), toadd);

            //se eliminan los archivos aniadisos
            for (String filePath : archivos) {
                try {
                    File f = new File(filePath);
                    long idImage = MediaUtils.isImageInGallery(f, this);
                    if (idImage != -1){
                        String extendion = FileUtils.getExtensionFile(f.getName());
                        if (com.rodrigo.lock.app.utils.MediaUtils.isExtensionImage(extendion)) {
                            MediaUtils.deleteImageGallery(idImage, this);
                        } else if (com.rodrigo.lock.app.utils.MediaUtils.isExtensionVideo(extendion)) {
                            MediaUtils.deleteVideoGallery(idImage, this);
                        }
                    }
                    if (f.exists()) {
                        FileUtils.delete(f);
                    }
                } catch (Exception e) {
                }
            }
        } catch (Exception e) {
        } finally {
            Event eventEnd = new Event();
            eventEnd.setEventType(EventType.TERMINO_ANIADIR_ARCHICOS);
            eventEnd.setVaultPath(path);
            EventBus.getInstance().addEvent(eventEnd);

        }
        mNotifyManager.cancel(id);
    }


}