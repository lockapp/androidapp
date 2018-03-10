package com.rodrigo.lock.app.sync;

import android.support.annotation.NonNull;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.query.Query;

/**
 * Created by Rodrigo on 27/12/2017.
 */


public class Backup {

    public interface BackupService{

        void init(@NonNull final BackupClient activity);

        void start();

        void stop();

        GoogleApiClient getClient();

        public void uploadToDrive(DriveId mFolderDriveId);

        public Query createQueryDeArchivosEnAppFolder(String nombreArchivo);

        public Query createQueryTodosAppFolder() ;
    }

    public interface  BackupClient {
        void terminoSubirArchivo();
        void terminoElimarViejos() ;
        void showErrorDialog();
        void finish();
        void showSuccessDialog();
    }
}