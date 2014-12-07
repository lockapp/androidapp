package com.rodrigo.lock.app.presentation;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.rodrigo.lock.app.Core.Clases.Accion;
import com.rodrigo.lock.app.Core.Interfaces.IPreferences;
import com.rodrigo.lock.app.Core.Manejadores.ManejadorCrypto;
import com.rodrigo.lock.app.Core.Manejadores.ManejadorFile;
import com.rodrigo.lock.app.Core.controllers.FileController;
import com.rodrigo.lock.app.Core.controllers.PreferencesController;
import com.rodrigo.lock.app.Core.controllers.crypto.CryptoController;
import com.rodrigo.lock.app.Core.controllers.crypto.DecryptControllerSeeMedia;
import com.rodrigo.lock.app.R;
import com.rodrigo.lock.app.presentation.SeeMedia.GridMediaActivity;
import com.rodrigo.lock.app.services.ExtractService;
import com.rodrigo.lock.app.services.SeeMediaService;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class DecryptActivity extends LockActivity {
    @InjectView(R.id.password)    EditText password;
    //@InjectView(R.id.imageView)    ImageView imageView;
    @InjectView(R.id.dobleopcion)   View dobleopcion;
    @InjectView(R.id.extraer2)   Button extraer2;

    String inFS;
    boolean vistaSegura = false;

    IPreferences preferences;
    FileController controller;
    String pass1;
    CryptoController cc;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decrypt);
        ButterKnife.inject(this);

        int idC = getIntent().getExtras().getInt("controlerId");
        this.controller = ManejadorFile.getControlador(idC);
        preferences = PreferencesController.getPreferencesController(this);

        // regularAccion();
        if (controller.getAccion() != Accion.DesencryptarConImagen ){
            dobleopcion.setVisibility(View.GONE);
            extraer2.setVisibility(View.VISIBLE);
        }else{

        }

    }



    @OnClick(R.id.extraer) void extraer() {
        pass1 = password.getText().toString();

        this.showProgress(true);
        controller.setPassword(pass1);
        new DesencriptarAsincronaService().execute();

    }


    @OnClick(R.id.extraer2) void extraer2() {
        extraer();
    }

    @OnClick(R.id.abrir) void abrir() {
        pass1 = password.getText().toString();

        //chequea la contraseña
       /* if (TextUtils.isEmpty(pass1)) {
            password.setError(getResources().getString(R.string.empty_password));

        }*/

            this.showProgress(true);
            controller.setPassword(pass1);
            new DesencriptarAsincronaSeeImage().execute();


    }




    private void showImage(){
        password.setText("");
        int ccid = ManejadorCrypto.add(cc);

        Intent s = new Intent(getApplicationContext(), SeeMediaService.class);
        s.putExtra("controlerId", ccid);
        startService(s);

        Intent i = new Intent(this,GridMediaActivity.class );
        i.putExtra("controlerId", ccid);
        startActivity(i);

        ManejadorFile.quitarControldor(controller.getId());
        finish();

    }



    public void convertirEnService() {
        int ccid = ManejadorCrypto.add(cc);

        Intent i = new Intent(getApplicationContext(), ExtractService.class);
        i.putExtra("controlerId", ccid);
        startService(i);

        ManejadorFile.quitarControldor(controller.getId());
        finish();
    }







    private class DesencriptarAsincronaService extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            try {
                controller.chequear();
                cc = controller.getDecryptController(DecryptActivity.this.getApplicationContext());
                cc.checkAndInit();
                return null;
            } catch (Exception e) {
                return (e.getMessage() == null)? "Error" :  e.getMessage();
            }
        }


        @Override
        protected void onPostExecute(String error) {
            showProgress(false);
            if (error == null) {
                convertirEnService();
            } else {
                mostrarError(error);
            }
        }

        @Override
        protected void onCancelled() {
            showProgress(false);
        }

    }







    private class DesencriptarAsincronaSeeImage extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            try {
                controller.chequear();
                cc = controller.getDecryptControllerSeeMedia(DecryptActivity.this.getApplicationContext());
                cc.checkAndInit();
                ((DecryptControllerSeeMedia) cc).loadImage();
                return null;

            } catch (Exception e) {
                return  e.getMessage();
            }
        }


        @Override
        protected void onPostExecute(String error) {
            showProgress(false);
            if (error == null) {
                showImage();
            } else {
                mostrarError(error);
            }
        }

        @Override
        protected void onCancelled() {
            showProgress(false);
        }

    }






}
