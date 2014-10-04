package com.rodrigo.lock.app.presentation.Encrypt;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.rodrigo.lock.app.Core.Clases.Archivo;
import com.rodrigo.lock.app.R;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Rodrigo on 27/09/2014.
 */
public class ReciveImageAndVideo extends ReceiveAndEncryptActivity{


    @Override
    public void encontrAraccion() {
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();


        if (Intent.ACTION_SEND.equals(action) ) {
                handle1Multimedia(intent);

        } else if (Intent.ACTION_SEND_MULTIPLE.equals(action)) {
                handleMultipleMultimedia(intent);
        }

    }



    void handle1Multimedia(Intent intent) {
        Uri uri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (uri != null) {
            String dir = getMediaFilePathFromUri(uri, this);
            if (TextUtils.isEmpty(dir)){
                ImagenNoValida(getResources().getString(R.string.error_nofind));
            }else{
                File f = new File(dir);
                Archivo a= new Archivo(f);
                controler.addFile(a);
                resolverAccion();
            }

        }
    }


      void handleMultipleMultimedia(Intent intent) {
        ArrayList<Uri> imageUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
        if (imageUris != null) {
            // Update UI to reflect multiple images being shared
            for (Uri u : imageUris) {
                if (u != null) {
                    String dir = getMediaFilePathFromUri(u, this);
                    if (TextUtils.isEmpty(dir)){
                        ImagenNoValida(getResources().getString(R.string.error_nofind));
                        return;
                    } else{
                        File f = new File(dir);
                        Archivo a= new Archivo(f);
                        controler.addFile(a);
                    }

                }
            }
            resolverAccion();


        }
    }


    private String getMediaFilePathFromUri(Uri uri, Context context) {
        String filePath;
        String[] filePathColumn = {MediaStore.MediaColumns.DATA};

        Cursor cursor =  context.getContentResolver().query(uri, filePathColumn, null, null, null);
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        filePath = cursor.getString(columnIndex);
        cursor.close();
        return filePath;
    }






}
