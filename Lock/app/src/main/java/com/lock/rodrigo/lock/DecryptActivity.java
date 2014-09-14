package com.lock.rodrigo.lock;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.lock.rodrigo.lock.Core.Clases.Accion;
import com.lock.rodrigo.lock.Core.crypto.DecryptController;
import com.lock.rodrigo.lock.Core.crypto.DecryptControllerSeeImage;
import com.lock.rodrigo.lock.Core.Controladores.FileController;
import com.lock.rodrigo.lock.Core.Controladores.PreferencesController;
import com.lock.rodrigo.lock.Core.Interfaces.IPreferences;
import com.lock.rodrigo.lock.Core.Manejadores.ManejadorFile;
import com.lock.rodrigo.lock.SeeImage.SeeImageActivity;

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
        }

    }



    @OnClick(R.id.extraer) void extraer() {
        pass1 = password.getText().toString();

        //chequea la contraseña
        if (TextUtils.isEmpty(pass1)) {
            password.setError(getResources().getString(R.string.empty_password));

        }else{
            this.showProgress(true);
            controller.setPassword(pass1);
            new DesencriptarAsincronaService().execute();
        }
    }


    @OnClick(R.id.extraer2) void extraer2() {
        extraer();
    }

    @OnClick(R.id.abrir) void abrir() {
        pass1 = password.getText().toString();

        //chequea la contraseña
        if (TextUtils.isEmpty(pass1)) {
            password.setError(getResources().getString(R.string.empty_password));

        }else{

            this.showProgress(true);
            controller.setPassword(pass1);
            new DesencriptarAsincronaSeeImage().execute();

        }
    }




    private void showImage(){
        password.setText("");
        Intent i = new Intent(this,SeeImageActivity.class );
        i.putExtra("controlerId", controller.getId());
        startActivity(i);
        this.finish();

    }



    private class DesencriptarAsincronaService extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            try {
                controller.chequear();
                DecryptController d =new DecryptController(controller,DecryptActivity.this);
                d.checkAndInit();
                controller.setCryptoController(d);
                controller.convertirenService(DecryptActivity.this);
                return null;

            } catch (Exception e) {
                return (e.getMessage() == null)? "Error" :  e.getMessage();
            }
        }


        @Override
        protected void onPostExecute(String error) {
            showProgress(false);
            if (error == null) {
                finish();
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
                DecryptControllerSeeImage d =new DecryptControllerSeeImage(controller,DecryptActivity.this);
                d.checkAndInit();
                controller.setCryptoController(d);
                d.loadImage();
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
