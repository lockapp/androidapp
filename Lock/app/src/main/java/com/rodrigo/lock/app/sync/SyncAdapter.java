package com.rodrigo.lock.app.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.DriveId;
import com.rodrigo.lock.app.Constants;
import com.rodrigo.lock.app.data.source.Preferences;


/**
 * Created by Rodrigo on 27/01/2018.
 */



public class SyncAdapter extends AbstractThreadedSyncAdapter implements Backup.BackupClient {
    private String TAG = "--r> SyncAdapter_backup";

    private Context context;
    private final ContentResolver contentResolver;

    public SyncAdapter(Context context, boolean autoInitialize) {
        this(context, autoInitialize, false);
        this.context = context;
    }

    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        //SIMICservice= SimicServiceFactory.createSimicServiceLogueado(context.getApplicationContext());
        this.contentResolver = context.getContentResolver();
        this.context = context;
    }


    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider,
                              SyncResult syncResult) {
        try {
            String backupFolder = backupFolder = Preferences.getPreference(Constants.Preferences.PREFERENCE_BACKUP_FOLDER, "");
            boolean respaldo = Preferences.getPreference(Constants.Preferences.PREFERENCE_RESPALDO_AUTOMATICO, true);

            if (!TextUtils.isEmpty(backupFolder) && respaldo) {

                Backup.BackupService backup = null;
                GoogleApiClient mGoogleApiClient = null;

                if (mGoogleApiClient == null) {
                    backup = new GoogleDriveBackup();
                    backup.init(this);
                }

                backup.start();
                backup.uploadToDrive(DriveId.decodeFromString(backupFolder));

                //backup.stop();
            }


        } catch (Throwable e) {
            //LogUtils.reportErrorToFirebase(e, "no se pudo ejecutar el sync adapter");
            Log.e(TAG, "No se pudo sincronizar", e);
            ++syncResult.stats.numIoExceptions;
        }
    }


    @Override
    public void terminoSubirArchivo() {

    }

    @Override
    public void terminoElimarViejos() {

    }

    @Override
    public void showErrorDialog() {

    }

    @Override
    public void finish() {

    }

    @Override
    public void showSuccessDialog() {

    }
}
