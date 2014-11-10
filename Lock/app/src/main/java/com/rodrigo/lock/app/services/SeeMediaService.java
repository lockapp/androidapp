package com.rodrigo.lock.app.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.Context;
import android.support.v4.app.NotificationCompat;

import com.rodrigo.lock.app.Core.Manejadores.ManejadorCrypto;
import com.rodrigo.lock.app.Core.controllers.FileController;
import com.rodrigo.lock.app.Core.Manejadores.ManejadorFile;
import com.rodrigo.lock.app.Core.controllers.crypto.CryptoController;
import com.rodrigo.lock.app.Core.controllers.crypto.DecryptControllerSeeMedia;
import com.rodrigo.lock.app.R;

import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class SeeMediaService extends IntentService {

    static int id = 0;
    NotificationManager mNotifyManager;
    NotificationCompat.Builder mBuilder;
    DecryptControllerSeeMedia controllerImage;

    public SeeMediaService() {
        super("SeeMediaService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        mNotifyManager = (NotificationManager) getApplication().getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(this);
        id++;
        mBuilder.setContentTitle(getResources().getString(R.string.working));
        //mBuilder.setContentText(getResources().getString(R.string.workingU));
        mBuilder.setSmallIcon(R.drawable.ic_action_not_secure);
        mBuilder.setProgress(0, 0, true);
        mBuilder.setOngoing(true);
        startForeground(id, mBuilder.build());


        if (intent != null) {
            int idC = intent.getExtras().getInt("controlerId");
            manejarMedia(idC);
           // manejarMediaParalelo(idC);
        }

        mNotifyManager.cancel(id);


    }


    public void manejarMedia(int idC){
        try {
            controllerImage= (DecryptControllerSeeMedia)ManejadorCrypto.getControlador(idC);
            while (!controllerImage.isComplete()) {
                controllerImage.loadImage();
            }
        }catch (Exception e){
       }

    }







}
