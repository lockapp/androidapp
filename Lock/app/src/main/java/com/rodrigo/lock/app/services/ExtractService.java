package com.rodrigo.lock.app.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.rodrigo.lock.app.Core.Clases.Accion;
import com.rodrigo.lock.app.Core.Clases.Archivo;
import com.rodrigo.lock.app.Core.Manejadores.ManejadorCrypto;
import com.rodrigo.lock.app.Core.Manejadores.ManejadorFile;
import com.rodrigo.lock.app.Core.Utils.Utils;
import com.rodrigo.lock.app.Core.controllers.FileController;
import com.rodrigo.lock.app.Core.controllers.crypto.CryptoController;
import com.rodrigo.lock.app.Core.controllers.crypto.EncryptController;
import com.rodrigo.lock.app.R;
import com.rodrigo.lock.app.presentation.DecryptActivity;
import com.rodrigo.lock.app.presentation.ErrorActivity;

import java.io.File;

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
        int idcc = intent.getExtras().getInt("controlerId");
        CryptoController cc= ManejadorCrypto.getControlador(idcc);

        try {

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
                cc.realizarTrabajo(this);

                finNorification();


                if (cc.getAccion() == Accion.Encyptar || cc.getAccion() == Accion.EncryptarConImagen) {
                    shareNotification(((EncryptController)cc).getToEncrypt());
                }
            }

        } catch (Exception e) {
            terminoConError(e.getMessage(), /*fc.getInF().getAbsolutePath()*/"");
        }

        //chequear esto aca
        ManejadorCrypto.quitarControldor(idcc);

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



    public void shareNotification(String c){
        try{
            //se crea intent to share
            id++;
            Intent sendIntent = Utils.shareExludingApp(this, Uri.fromFile(new File(c)));
            PendingIntent mapPendingIntent =   PendingIntent.getActivity(this, 0, Intent.createChooser(sendIntent, getResources().getText(R.string.send_to)), 0);

            //se crea intent para abrir
            FileController controler = ManejadorFile.createControler(getApplicationContext());
            Archivo a = new Archivo(new File(c));
            controler.addFile(a);
            controler.resolverAccion();
            Intent i = new Intent(this, DecryptActivity.class);
            i.putExtra("controlerId", controler.getId());
            PendingIntent contentIntent = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_ONE_SHOT);


            //se crea notificacion
            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(this.getApplicationContext())
                            .setSmallIcon(R.drawable.ic_action_secure)
                            .setContentTitle( getString(R.string.yourdatasafe))
                            .setContentText( getString(R.string.workcomplete))
                            .setContentIntent(contentIntent)
                            .setAutoCancel(true)
                            .addAction(R.drawable.ic_action_share,
                                    getString(R.string.action_share), mapPendingIntent);



            // Get an instance of the NotificationManager service
            NotificationManagerCompat notificationManager =  NotificationManagerCompat.from(this);
            // Build the notification and issues it with notification manager.
            notificationManager.notify(id, notificationBuilder.build());


        }catch (Exception e){

        }

    }

}