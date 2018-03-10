package com.rodrigo.lock.app.migracion;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.rodrigo.lock.app.Constants;
import com.rodrigo.lock.app.LockApplication;
import com.rodrigo.lock.app.data.converters.FileConverter;
import com.rodrigo.lock.app.R;
import com.rodrigo.lock.core.datatype.AddFileListener;
import com.rodrigo.lock.core.datatype.INewFile;
import com.rodrigo.lock.core.utils.FileUtils;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by Rodrigo on 29/05/14.
 */
public class MigracionService extends IntentService {



    static int id = 0;
    NotificationManager mNotifyManager;
    NotificationCompat.Builder mBuilder;

    public MigracionService() {
        super("ServiceManejador");
    }

    private static int count =0;
    private static Map<Integer, MigrationExecutor> pendientes = new HashMap();

    public static synchronized  int addPendientes(MigrationExecutor e){
        count++;
        pendientes.put(count, e);
        return count;
    }

    public static synchronized  void removePendiente(Integer id){
        pendientes.remove(id);
    }



    @Override
    protected void onHandleIntent(Intent intent) {
        id++;

        mNotifyManager = (NotificationManager) getApplication().getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentTitle(getResources().getString(R.string.migrando_vault));
        //mBuilder.setContentText(getResources().getString(R.string.workingU));
        mBuilder.setSmallIcon(R.drawable.ic_action_not_secure);
        mBuilder.setProgress(0, 0, true);
        mBuilder.setOngoing(true);
        startForeground(id, mBuilder.build());


        int idcc = intent.getExtras().getInt(Constants.CRYPTO_CONTROLLER);
        try {

            MigrationExecutor executor=pendientes.get(idcc);

            if (executor != null) {
                //String fileName = "tem_converter_" + id;
                File baseFolder= new File(com.rodrigo.lock.app.Constants.Storage.DEFAULT_EXTRACT_VAULT_DIRECTORY + id);
                if (baseFolder.exists()) {
                    FileUtils.delete(baseFolder);
                }
                baseFolder.mkdirs();

                executor.checkAndInit();
                executor.extraerTodosLosArchivos(baseFolder);

                LinkedList<File> files = new LinkedList();
                FileUtils.getFileAndSubFiles(files, baseFolder);
                LinkedList<INewFile> toVault = new LinkedList();
                for (File f : files){
                    toVault.add(FileConverter.getFileToVault(f));
                }
                executor.getOut().addFilesWithPreview( new AddFileListener(), toVault);

                if (baseFolder.exists()) {
                   FileUtils.delete(baseFolder);
                }
            }

            LockApplication.requestForcedSync();

        } catch (Exception e) {
            //terminoConError(e.getMessage(), /*fc.getInF().getAbsolutePath()*/"");
        }
        removePendiente(idcc);
        finNorification();
    }





    public void finNorification() {
        mNotifyManager.cancel(id);
    }
/*
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
*/


//    public void shareNotification(String c, String name){
//        try{
//            //se crea intent to share
//            id++;
//            Intent sendIntent = Utils.shareExludingApp(this, Uri.fromFile(new File(c)));
//            PendingIntent mapPendingIntent =   PendingIntent.getActivity(this, 0, Intent.createChooser(sendIntent, getResources().getText(R.string.send_to)), 0);
//
//            //se crea intent para abrir
//            FileController controler = ManejadorFile.createControler(getApplicationContext());
//            Archivo a = new Archivo(new File(c));
//            controler.addFile(a);
//            controler.resolverAccion();
//            Intent i = new Intent(this, DecryptActivity.class);
//            i.putExtra(Constants.FILE_CONTROLLER, controler.getId());
//            PendingIntent contentIntent = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_ONE_SHOT);
//
//
//            //se crea notificacion
//            NotificationCompat.Builder notificationBuilder =
//                    new NotificationCompat.Builder(this.getApplicationContext())
//                            .setSmallIcon(R.drawable.ic_action_secure)
//                            .setContentTitle( getString(R.string.yourdatasafe))
//                            .setContentText( getString(R.string.workcomplete) + " (" + name + ")")
//                            .setContentIntent(contentIntent)
//                            .setAutoCancel(true)
//                            .addAction(R.drawable.ic_action_share,
//                                    getString(R.string.action_share), mapPendingIntent);
//
//
//
//            // Get an instance of the NotificationManager service
//            NotificationManagerCompat notificationManager =  NotificationManagerCompat.from(this);
//            // Build the notification and issues it with notification manager.
//            notificationManager.notify(id, notificationBuilder.build());
//
//
//        }catch (Exception e){
//
//        }
//
//    }

}