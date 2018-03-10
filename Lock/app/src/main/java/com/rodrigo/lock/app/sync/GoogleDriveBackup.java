package com.rodrigo.lock.app.sync;

/**
 * Created by Rodrigo on 27/12/2017.
 */

import android.app.Activity;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import com.google.android.gms.drive.query.SortOrder;
import com.google.android.gms.drive.query.SortableField;
import com.rodrigo.lock.app.Constants;
import com.rodrigo.lock.app.LockApplication;
import com.rodrigo.lock.app.data.Clases.GlucosioBackup;
import com.rodrigo.lock.app.data.source.VaultsRepository;
import com.rodrigo.lock.app.old.Core.Utils.Utils;
import com.rodrigo.lock.core.utils.FileUtils;
//import com.google.firebase.crash.FirebaseCrash;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;


public class GoogleDriveBackup implements Backup.BackupService, GoogleApiClient.OnConnectionFailedListener {
    @Nullable
    private GoogleApiClient googleApiClient;

    @Nullable
    private WeakReference<Backup.BackupClient> activityRef;
/*
    private static final int REQUEST_CODE_CAPTURE_IMAGE = 1;
    private static final int REQUEST_CODE_CREATOR = 2;
    private static final int REQUEST_CODE_RESOLUTION = 3;
*/
    @Override
    public void init(@NonNull final Backup.BackupClient activity) {
        this.activityRef = new WeakReference<>(activity);

        googleApiClient = new GoogleApiClient.Builder(LockApplication.getAppContext())
                .addApi(Drive.API)
                .addScope(Drive.SCOPE_FILE)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        Log.d("Connection start", " conectado");
                        // Do nothing
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        Log.d("Connection deteniida", " suspendio");

                    }
                })
                .addOnConnectionFailedListener(this)
                .build();

    }

    @Override
    public GoogleApiClient getClient() {
        return googleApiClient;
    }

    @Override
    public void start() {
        if (googleApiClient != null) {
            googleApiClient.connect();
        } else {
            throw new IllegalStateException("You should call init before start");
        }
    }

    @Override
    public void stop() {
        if (googleApiClient != null) {
            googleApiClient.disconnect();
        } else {
            throw new IllegalStateException("You should call init before start");
        }
    }

    @Override
    public void onConnectionFailed(@NonNull final ConnectionResult result) {
       Log.i("Connection Failed", "GoogleApiClient connection failed: " + result.toString());

        if (activityRef != null && activityRef.get() != null) {
            Backup.BackupClient a = activityRef.get();
            if ((a instanceof Activity)) {
                Activity actividad = (Activity) a;

                if (!result.hasResolution()) {
                    // show the localized error dialog.
                    Log.d("error", "cannot resolve connection issue");
                    GoogleApiAvailability.getInstance().getErrorDialog(actividad, result.getErrorCode(), 0).show();
                    return;
                }


                if (result.hasResolution()) {
                    // show the localized error dialog.
                    try {
                        //result.startResolutionForResult(a, REQUEST_CODE_RESOLUTION);
                        result.startResolutionForResult(actividad, 1);
                    } catch (IntentSender.SendIntentException e) {
                        //FirebaseCrash.log("Drive connection failed");
                        //FirebaseCrash.report(e);
                        e.printStackTrace();
                        //GoogleApiAvailability.getInstance().getErrorDialog(a, result.getErrorCode(), 0).show();
                    }
                }

            }
        }
    }


    public void uploadToDrive(DriveId mFolderDriveId) {
        DriveFolder folder = mFolderDriveId.asDriveFolder();
        List<File> vaults = VaultsRepository.getVaultsFiles();
        for (File vault: vaults){
            eliminarArchivosViejosYsubirNuevoSiCorresponde(folder, vault);
        }
    }





    private void eliminarArchivosViejosYsubirNuevoSiCorresponde(final DriveFolder folder, final File vault){
        Query query = createQueryDeArchivosEnAppFolder(vault.getName());
        folder.queryChildren(googleApiClient, query)
                .setResultCallback(new ResultCallback<DriveApi.MetadataBufferResult>() {
                    private ArrayList<GlucosioBackup> backupsArray = new ArrayList<>();

                    @Override
                    public void onResult(@NonNull DriveApi.MetadataBufferResult result) {
                        Date ultimaActualizacion=null;
                        MetadataBuffer buffer = result.getMetadataBuffer();
                        int size = buffer.getCount();
                        if (size > 0) {
                            Metadata metadata = buffer.get(0);
                            ultimaActualizacion =metadata.getModifiedDate();
                        }
                        if (size > Constants.Backup.MAX_ARCHIVOS_RESPALDO) {
                            for (int i = Constants.Backup.MAX_ARCHIVOS_RESPALDO; i < size; i++) {
                                Metadata metadata = buffer.get(i);
                                if (metadata.isTrashable() && !metadata.isTrashed()) {
                                    DriveId driveId = metadata.getDriveId();
                                    driveId.asDriveFile().trash(googleApiClient).setResultCallback(new ResultCallback<Status>() {
                                        @Override
                                        public void onResult(@NonNull Status status) {
                                            //System.out.print("s");
                                        }
                                    });

                                }
                            }
                        }

                        Date fechaArchivo = new Date(vault.lastModified());
                        if ( ultimaActualizacion == null  || ultimaActualizacion.compareTo(fechaArchivo)<= 0){
                            subirArchivo(folder, vault);
                        }

                    }
                });
    }


    public void subirArchivo(final DriveFolder folder, final File vault){
        Drive.DriveApi.newDriveContents(googleApiClient)
                .setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {
                    @Override
                    public void onResult(@NonNull DriveApi.DriveContentsResult result) {
                        if (!result.getStatus().isSuccess()) {
                            //Log.e(TAG, "Error while trying to create new file contents");
                            Backup.BackupClient a = activityRef.get();
                            if (a != null){
                                a.showErrorDialog();
                            }
                            a.terminoSubirArchivo();
                            return;
                        }
                        final DriveContents driveContents = result.getDriveContents();

                        // Perform I/O off the UI thread.
                        new Thread() {
                            @Override
                            public void run() {
                                try{
                                    // write content to DriveContents
                                    OutputStream outputStream = driveContents.getOutputStream();
                                    FileInputStream inputStream = new FileInputStream(vault);

                                    try {
                                        FileUtils.copy(inputStream, outputStream);
                                    }finally {
                                        outputStream.close();
                                        inputStream.close();
                                    }


                                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                            .setTitle(vault.getName())
                                            .setMimeType("text/plain")
                                            .build();

                                    // create a file in selected folder
                                    folder.createFile(googleApiClient, changeSet, driveContents)
                                            .setResultCallback(new ResultCallback<DriveFolder.DriveFileResult>() {
                                                @Override
                                                public void onResult(@NonNull DriveFolder.DriveFileResult result) {
                                                    Backup.BackupClient a = activityRef.get();
                                                    if (!result.getStatus().isSuccess()) {
                                                        //Log.d(TAG, "Error while trying to create the file");
                                                        if (a != null){
                                                            a.showErrorDialog();
                                                            a.finish();
                                                            a.terminoSubirArchivo();
                                                        }
                                                        return;
                                                    }
                                                    if (a != null){
                                                        a.showSuccessDialog();
                                                        //a.finish();
                                                        a.terminoSubirArchivo();
                                                    }
                                                }
                                            });

                                } catch (Exception e) {
                                    Backup.BackupClient a = activityRef.get();
                                    if (a != null){
                                        a.showErrorDialog();
                                    }
                                    e.printStackTrace();
                                }
                            }
                        }.start();
                    }
                });
    }







    public Query createQueryDeArchivosEnAppFolder(String nombreArchivo) {
        SortOrder sortOrder = new SortOrder.Builder().addSortDescending(SortableField.MODIFIED_DATE).build();
        Query query = new Query.Builder()
               .addFilter(Filters.eq(SearchableField.TITLE, nombreArchivo))
                .addFilter(Filters.eq(SearchableField.TRASHED, false))
                .setSortOrder(sortOrder)
                .build();
        return query;
    }



    public Query createQueryTodosAppFolder() {
        SortOrder sortOrder = new SortOrder.Builder().addSortDescending(SortableField.MODIFIED_DATE).build();
        Query query = new Query.Builder()
                .addFilter(Filters.contains(SearchableField.TITLE,".lock" ))
                .addFilter(Filters.eq(SearchableField.TRASHED, false))
                .setSortOrder(sortOrder)
                .build();
        return query;
    }

}