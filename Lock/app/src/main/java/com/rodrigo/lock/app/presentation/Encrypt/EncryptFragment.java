package com.rodrigo.lock.app.presentation.Encrypt;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.rodrigo.lock.app.Core.Clases.Accion;
import com.rodrigo.lock.app.Core.Clases.Archivo;
import com.rodrigo.lock.app.Core.Clases.FileType;
import com.rodrigo.lock.app.Core.Interfaces.IPreferences;
import com.rodrigo.lock.app.Core.Manejadores.ManejadorCrypto;
import com.rodrigo.lock.app.Core.Manejadores.ManejadorFile;
import com.rodrigo.lock.app.Core.Utils.MediaUtils;
import com.rodrigo.lock.app.Core.controllers.FileController;
import com.rodrigo.lock.app.Core.controllers.PreferencesController;
import com.rodrigo.lock.app.Core.controllers.crypto.CryptoController;
import com.rodrigo.lock.app.R;
import com.rodrigo.lock.app.presentation.UI.TextureVideoView;
import com.rodrigo.lock.app.services.ExtractService;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by Rodrigo on 12/07/2014.
 */


public class EncryptFragment extends Fragment {
    @InjectView(R.id.textureView)
    com.rodrigo.lock.app.presentation.UI.TextureVideoView videoVew;

    @InjectView(R.id.bg)
    ImageView bg;

    @InjectView(R.id.bgfondo)
    ImageView bgfondo;

    @InjectView(R.id.fondoconimgen)
    FrameLayout fondoconimgen;


    @InjectView(R.id.size)
    TextView size;
    //  @InjectView(R.id.fecha)    TextView fecha;
    @InjectView(R.id.nombre)
    TextView nombre;


    @InjectView(R.id.password1)
    EditText password1;
    @InjectView(R.id.password2)
    EditText password2;

    ReceiveAndEncryptActivity padre;
    FileController controler;

    int imgIter= 0;
    Bitmap actualImg = null;

    public EncryptFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //    super.onCreateView(inflater, container, savedInstanceState);
        View V = inflater.inflate(R.layout.fragment_encrypt, container, false);
        ButterKnife.inject(this, V);

        padre = (ReceiveAndEncryptActivity) this.getActivity();
        controler = padre.getControler();
        init();
        return V;
    }




    public void init() {
        size.setText(controler.getSizesOfFiles());
        nombre.setText(controler.getName());

        if (controler.getAccion() != Accion.EncryptarConImagen){
            fondoconimgen.setVisibility(View.GONE);
        } /*else {
            // if (actualImg == null) actualImg = ImgUtils.TransformImage(controler.getInFiles().get(imgIter).getFile().getAbsolutePath());
            if (actualImg == null){
                bg.setImageBitmap(actualImg);
                bg.setVisibility(View.VISIBLE);
            }
            //  bg.setImageBitmap(actualImg);
        }*/

        IPreferences preferencias = PreferencesController.getPreferencesController(padre);
        if (preferencias.getEncryptarMismaPassword()) {
            password1.setText(preferencias.getPassword());
            password2.setText(preferencias.getPassword());
        }

    }


    @OnClick(R.id.bloquear)
    void encrypt() {
        Boolean cancel = false;

        String pass1 = password1.getText().toString();
        String pass2 = password2.getText().toString();

        if (TextUtils.isEmpty(pass1)) {
            cancel = true;
            password1.setError( getResources().getString(R.string.empty_password));
        }

        if (TextUtils.isEmpty(pass2)) {
            cancel = true;
            password2.setError(getResources().getString(R.string.re_password));
        }


        if (!pass1.equals(pass2) && !cancel) {
            cancel = true;
            password1.setError(getResources().getString(R.string.nomatch_password));
            password2.setError(getResources().getString(R.string.nomatch_password));
        }

        try {
            if (!cancel) {
                controler.chequear();
                // padre.showProgress(true);
                controler.setPassword(pass1);
                controler.setCabezal(this.padre.getCabezal());

                CryptoController cc = controler.getEncryptController();
                int ccid = ManejadorCrypto.add(cc);

                Intent i = new Intent(padre.getApplicationContext(), ExtractService.class);
                i.putExtra("controlerId", ccid);
                padre.startService(i);

                ManejadorFile.quitarControldor(controler.getId());
                padre.finish();
            }

        } catch (Exception e) {
            padre.mostrarError(e.getMessage());
        }

    }

    actualizadorDeFotos adf=null;

    @Override
    public void onResume() {
        super.onResume();
        if (controler.getAccion() == Accion.EncryptarConImagen){
            adf = new actualizadorDeFotos();
            new Thread(adf).start();
        }


    }

    @Override
    public void onPause() {
        super.onPause();
        if (adf!= null){
           adf.setSalir(true);
        }
        if (videoVew != null) {
            videoVew.stop();
        }
    }

    Archivo actual;

    private class actualizadorDeFotos implements Runnable {
        boolean salir = false;
        public void setSalir(boolean salir) {
            this.salir = salir;
        }

        @Override
        public void run() {
            try {
                //Thread.sleep(5000);

                while (!salir) {
                    imgIter = imgIter % controler.getInFiles().size();

                    actual = controler.getInFiles().get(imgIter);
                    if (actual.getTipo() == FileType.Imagen){
                        actualImg = MediaUtils.TransformImage(actual.getFile().getAbsolutePath());
                    }
                    //ImgUtils.createBlur(padre, actualImg);

                    padre.runOnUiThread(new Runnable() {
                        public void run() {
                           updateBG();
                        }
                    });

                    if (controler.getInFiles().size() > 1){
                        Thread.sleep(5000);
                        imgIter++;
                    }else{
                        salir=true;
                    }

                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


    }


    public void updateBG(){
        if (actual.getTipo() == FileType.Imagen){
            bgfondo.setImageBitmap(actualImg);
            MediaUtils.ImageViewAnimatedChange(padre, bg, actualImg);

            videoVew.stop();
            bgfondo.setVisibility(View.VISIBLE);
            bg.setVisibility(View.VISIBLE);
            videoVew.setVisibility(View.GONE);

        }else {
            bg.setVisibility(View.GONE);
            bgfondo.setVisibility(View.GONE);
            videoVew.setVisibility(View.VISIBLE);

            videoVew.setScaleType(TextureVideoView.ScaleType.TOP);
            videoVew.setDataSource(actual.getFile().getAbsolutePath());
            videoVew.setLooping(true);
           // videoVew.mute();
            videoVew.play();
        }
    }





}
