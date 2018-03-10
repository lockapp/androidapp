package com.rodrigo.lock.app.mvp.backup;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.paolorotolo.expandableheightlistview.ExpandableHeightListView;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.DriveResource;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.OpenFileActivityBuilder;
import com.google.android.gms.drive.query.Query;
import com.rodrigo.lock.app.Constants;
import com.rodrigo.lock.app.R;
import com.rodrigo.lock.app.data.Clases.GlucosioBackup;
import com.rodrigo.lock.app.data.source.Preferences;
import com.rodrigo.lock.app.mvp.listVaults.VaultsActivity;
import com.rodrigo.lock.app.sync.Backup;
import com.rodrigo.lock.app.sync.GoogleDriveBackup;
import com.rodrigo.lock.core.EncryptedFileSystem;
import com.rodrigo.lock.core.EncryptedFileSystemHandler;
import com.rodrigo.lock.core.utils.FileUtils;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BackupActivity extends AppCompatActivity implements Backup.BackupClient {
    private static final int REQUEST_CODE_PICKER = 2;
    private static final int REQUEST_CODE_PICKER_FOLDER = 4;
    //
    private static final String TAG = "glucosio_drive_backup";
    private static final String BACKUP_FOLDER_KEY = "backup_folder";
    //
    private Backup.BackupService backup;
    private GoogleApiClient mGoogleApiClient;
    private IntentSender intentPicker;
    ;
    ////    //private Realm realm;
    private String backupFolder;
////
    //private SharedPreferences sharedPref;


    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.view_respaldo_sin_configurar)
    View respaldoSinConfigurar;
    @BindView(R.id.view_respaldo_configurado)
    View respaldoConfigurado;
    @BindView(R.id.activity_backup_drive_button_backup)
    Button backupButton;
    @BindView(R.id.activity_backup_drive_button_manage_drive)
    TextView manageButton;
    @BindView(R.id.activity_backup_drive_textview_folder)
    TextView folderTextView;
    @BindView(R.id.activity_backup_drive_button_folder)
    LinearLayout selectFolderButton;
    @BindView(R.id.activity_backup_drive_listview_restore)
    ExpandableHeightListView backupListView;
    @BindView(R.id.boton_sin_configurar)
    Button configurarRespaldosBTN;
    @BindView(R.id.respaldoAutomatico)
    CheckBox respaldoAutomatico;


    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.backup_activity);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.title_activity_backup_drive));

        backupListView.setExpanded(true);

        backupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                respaldar();
            }
        });
        selectFolderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seleccionarCarpetaAGuardarRespaldos();
            }
        });
        configurarRespaldosBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seleccionarCarpetaAGuardarRespaldos();
            }
        });

        manageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openOnDrive(DriveId.decodeFromString(backupFolder));
            }
        });


        boolean respaldo= Preferences.getPreference(Constants.Preferences.PREFERENCE_RESPALDO_AUTOMATICO, true);
        respaldoAutomatico.setChecked(respaldo);
        respaldoAutomatico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Preferences.savePreference(Constants.Preferences.PREFERENCE_RESPALDO_AUTOMATICO, respaldoAutomatico.isChecked());
            }
        });

    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mGoogleApiClient == null) {
            backup = new GoogleDriveBackup();
            backup.init(this);
        }

        backup.start();
        mGoogleApiClient = backup.getClient();

        backupFolder = Preferences.getPreference(Constants.Preferences.PREFERENCE_BACKUP_FOLDER, "");
        if (!TextUtils.isEmpty(backupFolder)) {
            respaldoSinConfigurar.setVisibility(View.GONE);
            respaldoConfigurado.setVisibility(View.VISIBLE);

            setBackupFolderTitle(DriveId.decodeFromString(backupFolder));
            manageButton.setVisibility(View.VISIBLE);

            //se carga la lista de respaldo si la carpeta no es vacia
            getBackupsFromDrive(getBackupFolder());
        } else {
            respaldoSinConfigurar.setVisibility(View.VISIBLE);
            respaldoConfigurado.setVisibility(View.GONE);
        }
    }


    public DriveFolder getBackupFolder() {
        return DriveId.decodeFromString(backupFolder).asDriveFolder();
        //return Drive.DriveApi.getAppFolder(mGoogleApiClient);
    }


    @Override
    protected void onPause() {
        if (mGoogleApiClient != null) {
            backup.stop();
            //mGoogleApiClient.disconnect();
        }
        super.onPause();
    }


    public void respaldar() {
        if (TextUtils.isEmpty(backupFolder)) {
            try {
                if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                    if (intentPicker == null)
                        intentPicker = buildIntent();
                    //Start the picker to choose a folder
                    startIntentSenderForResult(intentPicker, REQUEST_CODE_PICKER, null, 0, 0, 0);
                }
            } catch (IntentSender.SendIntentException e) {
                Log.e(TAG, "Unable to send intent", e);
                showErrorDialog();
            }
        } else {
            backup.uploadToDrive(DriveId.decodeFromString(backupFolder));
            //uploadToDrive(/**/);
        }
    }


    public void seleccionarCarpetaAGuardarRespaldos() {
        try {
            intentPicker = null;
            if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                if (intentPicker == null) {
                    intentPicker = buildIntent();
                }
                //Start the picker to choose a folder
                startIntentSenderForResult(intentPicker, REQUEST_CODE_PICKER_FOLDER, null, 0, 0, 0);
            }
        } catch (IntentSender.SendIntentException e) {
            Log.e(TAG, "Unable to send intent", e);
            showErrorDialog();
        }
    }


    private void setBackupFolderTitle(DriveId id) {
        id.asDriveFolder().getMetadata((mGoogleApiClient)).setResultCallback(
                new ResultCallback<DriveResource.MetadataResult>() {
                    @Override
                    public void onResult(@NonNull DriveResource.MetadataResult result) {
                        if (!result.getStatus().isSuccess()) {
                            showErrorDialog();
                            return;
                        }
                        Metadata metadata = result.getMetadata();
                        folderTextView.setText(metadata.getTitle());
                    }
                }
        );
    }


    private IntentSender buildIntent() {
        return Drive.DriveApi
                .newOpenFileActivityBuilder()
                .setMimeType(new String[]{DriveFolder.MIME_TYPE})
                .build(mGoogleApiClient);
    }



    private void getBackupsFromDrive(DriveFolder folder) {
        final Activity activity = this;
        Query query = backup.createQueryTodosAppFolder();
        folder.queryChildren(mGoogleApiClient, query)
                .setResultCallback(new ResultCallback<DriveApi.MetadataBufferResult>() {

                    private ArrayList<GlucosioBackup> backupsArray = new ArrayList<>();

                    @Override
                    public void onResult(@NonNull DriveApi.MetadataBufferResult result) {
                        MetadataBuffer buffer = result.getMetadataBuffer();
                        int size = buffer.getCount();
                        for (int i = 0; i < size; i++) {
                            Metadata metadata = buffer.get(i);
                            DriveId driveId = metadata.getDriveId();
                            Date modifiedDate = metadata.getModifiedDate();
                            long backupSize = metadata.getFileSize();
                            String title = metadata.getTitle();
                            backupsArray.add(new GlucosioBackup(title, driveId, modifiedDate, backupSize));
                        }
                        backupListView.setAdapter(new BackupAdapter(activity, R.layout.activity_backup_drive_restore_item, backupsArray));
                    }
                });
    }

    public void downloadFromDrive(final String archivoNombre, final DriveFile file) {
        file.open(mGoogleApiClient, DriveFile.MODE_READ_ONLY, null)
                .setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {
                    @Override
                    public void onResult(@NonNull DriveApi.DriveContentsResult result) {
                        if (!result.getStatus().isSuccess()) {
                            showErrorDialog();
                            return;
                        }

                        String filename = archivoNombre;
                        if (TextUtils.isEmpty(filename)){
                            DriveResource.MetadataResult mdRslt = file.getMetadata(mGoogleApiClient).await();
                            filename = mdRslt.getMetadata().getTitle();
                        }




                        // DriveContents object contains pointers
                        // to the actual byte stream
                        DriveContents contents = result.getDriveContents();
                        InputStream input = contents.getInputStream();
                        File baseFile = new File(Preferences.getDefaultVaultDirectory());
                        if (!baseFile.exists()) {
                            baseFile.mkdirs();
                        }
                        File file = new File(baseFile, filename + ".back.tmp");
                        File actual = new File(baseFile, filename );

                        try {
                            OutputStream output = new FileOutputStream(file);
                            try {
                                byte[] buffer = new byte[4 * 1024]; // or other buffer size
                                int read;

                                while ((read = input.read(buffer)) != -1) {
                                    output.write(buffer, 0, read);
                                }
                                output.flush();
                            } finally {
                                safeCloseClosable(output);
                                safeCloseClosable(input);
                            }


                            EncryptedFileSystemHandler.removeFromUso(actual.getAbsolutePath());
                            actual.delete();
                            file.renameTo(actual);


                        } catch (Exception e) {
                            //LogUtils.reportErrorToFirebase(e,  "Error downloading backup from drive, file not found");
//                            reportToFirebase(e, "Error downloading backup from drive, file not found");
                            e.printStackTrace();
                        } finally {
                            safeCloseClosable(input);
                        }

                        Toast.makeText(getApplicationContext(), R.string.activity_backup_drive_message_restaurada, Toast.LENGTH_LONG).show();

                        // Reboot app

//                        Intent mStartActivity = new Intent(getApplicationContext(), VaultsActivity.class);
//                        int mPendingIntentId = 123456;
//                        PendingIntent mPendingIntent = PendingIntent.getActivity(getApplicationContext(), mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
//                        AlarmManager mgr = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
//                        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 200, mPendingIntent);
//                        System.exit(0);


                        Intent newIntent = new Intent(getApplicationContext(),VaultsActivity.class);
                        newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(newIntent);

                        finish();
                    }
                });
    }

    private void safeCloseClosable(Closeable closeable) {
        try {
            closeable.close();
        } catch (IOException e) {
        //    LogUtils.reportErrorToFirebase(e,   "Error downloading backup from drive, IO Exception");
//            reportToFirebase(e, "Error downloading backup from drive, IO Exception");
            e.printStackTrace();
        }
    }



    private void openOnDrive(DriveId driveId) {
        driveId.asDriveFolder().getMetadata((mGoogleApiClient)).setResultCallback(
                new ResultCallback<DriveResource.MetadataResult>() {
                    @Override
                    public void onResult(@NonNull DriveResource.MetadataResult result) {
                        if (!result.getStatus().isSuccess()) {
                            showErrorDialog();
                            return;
                        }
                        Metadata metadata = result.getMetadata();
                        String url = metadata.getAlternateLink();
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                    }
                }
        );
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    backup.start();
                }
                break;
            // REQUEST_CODE_PICKER
            case REQUEST_CODE_PICKER:
                intentPicker = null;

                if (resultCode == RESULT_OK) {
                    //Get the folder drive id
                    DriveId mFolderDriveId = data.getParcelableExtra(OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);

                    saveBackupFolder(mFolderDriveId.encodeToString());

                    backup.uploadToDrive(DriveId.decodeFromString(backupFolder));
                    //uploadToDrive(/*mFolderDriveId*/);
                }
                break;

            // REQUEST_CODE_SELECT
            case 3:
                if (resultCode == RESULT_OK) {
                    // get the selected item's ID
                    DriveId driveId = data.getParcelableExtra(OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);

                    DriveFile file = driveId.asDriveFile();
                    downloadFromDrive(null, file);

                } else {
                    showErrorDialog();
                }
                finish();
                break;
            // REQUEST_CODE_PICKER_FOLDER
            case REQUEST_CODE_PICKER_FOLDER:
                if (resultCode == RESULT_OK) {
                    //Get the folder drive id
                    DriveId mFolderDriveId = data.getParcelableExtra(OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);

                    saveBackupFolder(mFolderDriveId.encodeToString());
                    // Restart activity to apply changes
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                }
                break;
        }
    }

    private void saveBackupFolder(String folderPath) {
        Preferences.savePreference(Constants.Preferences.PREFERENCE_BACKUP_FOLDER, folderPath);
    }

    public void showSuccessDialog() {
        Toast.makeText(getApplicationContext(), R.string.activity_backup_drive_success, Toast.LENGTH_SHORT).show();
        getBackupsFromDrive(getBackupFolder());
    }

    @Override
    public void terminoSubirArchivo() {

    }

    @Override
    public void terminoElimarViejos() {

    }

    //
    public void showErrorDialog() {
        Toast.makeText(getApplicationContext(), R.string.activity_backup_drive_failed, Toast.LENGTH_SHORT).show();
    }

    //

//

//
//    public void disconnectClient() {
//        backup.stop();
//    }
//
//    public boolean onOptionsItemSelected(MenuItem item) {
//        finish();
//        return true;
//    }
//
//    @Override
//    protected void attachBaseContext(Context newBase) {
//       // super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
//    }
}