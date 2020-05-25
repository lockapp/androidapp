package com.rodrigo.lock.app.extract;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import androidx.core.app.NotificationCompat;
import android.webkit.MimeTypeMap;

import com.rodrigo.lock.app.R;
import com.rodrigo.lock.app.bus.Event;
import com.rodrigo.lock.app.bus.EventBus;
import com.rodrigo.lock.app.bus.EventType;
import com.rodrigo.lock.app.data.source.Preferences;
import com.rodrigo.lock.app.utils.MediaUtils;
import com.rodrigo.lock.core.EncryptedFileSystem;
import com.rodrigo.lock.core.EncryptedFileSystemHandler;
import com.rodrigo.lock.core.clases.LockFile;
import com.rodrigo.lock.core.utils.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * Created by Rodrigo on 29/05/14.
 */
public class ExtractService extends IntentService {
    public static final String EXTRA_VAULT_PATH= "EXTRA_VAULT_PATH";
    public static final String EXTRA_VAULT_PASSWORD= "EXTRA_VAULT_PASSWORD";
    public static final String EXTRA_ID_ARCHIVO= "EXTRA_ID_ARCHIVO";


    static int id = 0;
    NotificationManager mNotifyManager;
    NotificationCompat.Builder mBuilder;

    public ExtractService() {
        super("MigracionService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        id++;

        mNotifyManager = (NotificationManager) getApplication().getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentTitle(getResources().getString(R.string.desbloqueando_archivo));
        //mBuilder.setContentText(getResources().getString(R.string.workingU));
        mBuilder.setSmallIcon(R.drawable.ic_action_secure);
        mBuilder.setProgress(0, 0, true);
        mBuilder.setOngoing(true);
        startForeground(id, mBuilder.build());


        String path= intent.getExtras().getString(EXTRA_VAULT_PATH);
        String password= intent.getExtras().getString(EXTRA_VAULT_PASSWORD);
        String idArchivo= intent.getExtras().getString(EXTRA_ID_ARCHIVO);

        try {
            Event eventIniti = new Event();
            eventIniti.setEventType(EventType.EMPEZANDO_EXTRAER_ARCHIVOS);
            eventIniti.setVaultPath(path);
            EventBus.getInstance().addEvent(eventIniti);

            EncryptedFileSystem fileSystem = EncryptedFileSystemHandler.openEncryptedFile(path, password);
            LockFile lockFile = fileSystem.getFile(idArchivo);


            String extractPath  = Preferences.getDefaultUnlockDirectory() + File.separator +  FileUtils.removeExtensionFile((new File(path)).getName());
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

            createOpenNotification(file);


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            Event eventEnd= new Event();
            eventEnd.setEventType(EventType.TERMINO_EXTRAER_ARCHICOS);
            eventEnd.setVaultPath(path);
            EventBus.getInstance().addEvent(eventEnd);

        }
        mNotifyManager.cancel(id);
    }



    public void createOpenNotification(File file){
        MimeTypeMap myMime = MimeTypeMap.getSingleton();
        String mimeType = myMime.getMimeTypeFromExtension(FileUtils.getExtensionFile(file.getName()));

        Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), mimeType);

        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

        Notification noti = new NotificationCompat.Builder(this)
                .setContentTitle(getString(R.string.archivo_desbloqueado_correcto))
                .setContentText(file.getAbsolutePath())
                .setSmallIcon(R.drawable.ic_action_not_secure)
                .setContentIntent(pIntent).build();

        noti.flags |= Notification.FLAG_AUTO_CANCEL;

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, noti);

    }


}