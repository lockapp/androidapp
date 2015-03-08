package com.rodrigo.lock.app.presentation.Encrypt;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.rodrigo.lock.app.Constants;
import com.rodrigo.lock.app.Core.Clases.Accion;
import com.rodrigo.lock.app.Core.Clases.Archivo;
import com.rodrigo.lock.app.Core.Clases.FileHeader;
import com.rodrigo.lock.app.Core.Manejadores.ManejadorFile;
import com.rodrigo.lock.app.Core.Utils.FileUtils;
import com.rodrigo.lock.app.Core.controllers.FileController;
import com.rodrigo.lock.app.R;
import com.rodrigo.lock.app.presentation.DecryptActivity;

import java.util.ArrayList;

/**
 * Created by Rodrigo on 27/09/2014.
 */
public class ReceiveActivity extends ActionBarActivity {

    FileController controler;
    FileHeader cabezal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        controler = ManejadorFile.createControler(getApplicationContext());
        encontrAraccion();
    }




    public void encontrAraccion() {
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null)
            handle1File(intent);
        else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null)
            handleMultipleFile(intent); // Handle multiple images being sent

    }



    void handle1File(final Intent intent) {
        Uri uri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if ( FileUtils.isLocalUri(uri)) {
            handleUri(uri);
            resolverAccion();
        }else {
            FileNotFound(uri);
        }
    }


    void handleMultipleFile(final Intent intent) {
        ArrayList<Uri> uris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
        for (Uri uri : uris){
            if ( FileUtils.isLocalUri(uri)){
                handleUri(uri);
            }else {
                FileNotFound(uri);
                return;
            }
        }
        resolverAccion();

    }




    void handleUri(final Uri data) {
        Archivo a= new Archivo(this, data);
        controler.addFile(a);
    }



    //resuelve si manda a desecryptar o se queda aca y lo encrypta
    void resolverAccion() {
        try {
            controler.resolverAccion();

            if ((controler.getAccion() == Accion.Encyptar) || (controler.getAccion() == Accion.EncryptarConImagen)) {
                cabezal = new FileHeader();

            } else {
                Intent i = new Intent(this, DecryptActivity.class);
                //i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //i.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK | Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
                i.putExtra(Constants.FILE_CONTROLLER, controler.getId());
                startActivity(i);

                finish();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }





    public void FileNotFound(Uri uri){
        String error;
        if (uri != null) {
            error = String.format(getResources().getString(R.string.error_notfound2), uri.toString());
        }else{
            error = getResources().getString(R.string.error_nofind);
        }

        new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.error_noblock))
                .setMessage(error)
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();


    }




}
