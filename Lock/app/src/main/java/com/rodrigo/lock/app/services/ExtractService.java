package com.rodrigo.lock.app.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.rodrigo.lock.app.Core.Clases.Accion;
import com.rodrigo.lock.app.Core.Manejadores.ManejadorCrypto;
import com.rodrigo.lock.app.Core.controllers.FileController;
import com.rodrigo.lock.app.Core.Manejadores.ManejadorFile;
import com.rodrigo.lock.app.Core.controllers.crypto.CryptoController;
import com.rodrigo.lock.app.Core.controllers.crypto.DecryptController;
import com.rodrigo.lock.app.Core.controllers.crypto.EncryptController;
import com.rodrigo.lock.app.Core.crypto.AES.Crypto;
import com.rodrigo.lock.app.presentation.ErrorActivity;
import com.rodrigo.lock.app.R;

/**
 * Created by Rodrigo on 29/05/14.
 */
public class ExtractService extends IntentService {


    static int id = 0;
    NotificationManager mNotifyManager;
    NotificationCompat.Builder mBuilder;

    public ExtractService() {
        super("ServiceManejador");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        mNotifyManager = (NotificationManager) getApplication().getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(this);
        id++;
        try {
            // If we get killed, after returning from here, restart
            int idcc = intent.getExtras().getInt("controlerId");
            CryptoController cc= ManejadorCrypto.getControlador(idcc);

            if (cc != null) {
                if (cc.getAccion() == Accion.Encyptar || cc.getAccion() == Accion.EncryptarConImagen) {
                    mBuilder.setContentTitle(getResources().getString(R.string.working)).setContentText(getResources().getString(R.string.workingL)).setSmallIcon(R.drawable.ic_action_secure);
                } else {
                    mBuilder.setContentTitle(getResources().getString(R.string.working)).setContentText(getResources().getString(R.string.workingU)).setSmallIcon(R.drawable.ic_action_not_secure);
                }
                mBuilder.setProgress(0, 0, true);
                mBuilder.setOngoing(true);
                startForeground(id, mBuilder.build());



                cc.checkAndInit();
                ManejadorCrypto.quitarControldor(idcc);
                cc.realizarTrabajo(this, id);

                finNorification();
            }

        } catch (Exception e) {
            terminoConError(e.getMessage(), /*fc.getInF().getAbsolutePath()*/"");
        }

    }


/*
    public void updateProgressBar(int id, int max, int incr) {
        mBuilder.setProgress(max, incr, false);
        // Displays the progress bar for the first time.
        mNotifyManager.notify(id, mBuilder.build());
    }

    public void setIndeterminateProgressBar(int id) {
        // Sets an activity indicator for an operation of indeterminate length
        mBuilder.setProgress(0, 0, true);
        // Issues the notification
        mNotifyManager.notify(id, mBuilder.build());
    }*/


    public void finNorification() {
        mNotifyManager.cancel(id);
    }

    public void terminoConError(String descripcionError, String path) {
        finNorification();
        id++;

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentTitle(getResources().getString(R.string.error)).setContentText(descripcionError).setSmallIcon(R.drawable.ic_action_error);

        Intent resultIntent = new Intent(this, ErrorActivity.class);
        resultIntent.putExtra("error", descripcionError);
        resultIntent.putExtra("file", path);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_ONE_SHOT);

        mBuilder.setContentIntent(contentIntent);
        mBuilder.setAutoCancel(true);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(id, mBuilder.build());

    }


}