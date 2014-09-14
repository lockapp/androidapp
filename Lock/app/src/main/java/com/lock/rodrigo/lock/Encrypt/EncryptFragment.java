package com.lock.rodrigo.lock.Encrypt;

import android.app.Fragment;
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

import com.lock.rodrigo.lock.Core.Clases.Accion;
import com.lock.rodrigo.lock.Core.Clases.InputType;
import com.lock.rodrigo.lock.Core.Controladores.FileController;
import com.lock.rodrigo.lock.Core.Controladores.PreferencesController;
import com.lock.rodrigo.lock.Core.Interfaces.IPreferences;
import com.lock.rodrigo.lock.Core.Utils.ImgUtils;
import com.lock.rodrigo.lock.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by Rodrigo on 12/07/2014.
 */


public class EncryptFragment extends Fragment {
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
        View V = inflater.inflate(R.layout.fragment_encrypt_image, container, false);
        ButterKnife.inject(this, V);

        padre = (ReceiveAndEncryptActivity) this.getActivity();
        controler = padre.getControler();
        init();
        return V;
    }




    public void init() {
        size.setText(controler.getSizesOfFiles());
        nombre.setText(controler.getName());
        if (controler.getVinoComo() == InputType.Image || controler.getVinoComo() == InputType.Images) {
            if (actualImg == null) actualImg = ImgUtils.TransformImage(controler.getInFiles().get(imgIter).getAbsolutePath());
            bg.setImageBitmap(actualImg);
        }else{
            fondoconimgen.setVisibility(View.GONE);
        }

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
                if (!this.padre.getCabezal().isDefault())
                    controler.setCabezal(this.padre.getCabezal());
                controler.setCryptoController(null);
                controler.convertirenService(padre);
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
        if (controler.getVinoComo() == InputType.Images){
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
    }


    private class actualizadorDeFotos implements Runnable {
        boolean salir = false;

        public void setSalir(boolean salir) {
            this.salir = salir;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(5000);

                while (!salir) {
                    imgIter++;
                    imgIter = imgIter % controler.getInFiles().size();
                    actualImg = ImgUtils.TransformImage(controler.getInFiles().get(imgIter).getAbsolutePath());
                   // ImgUtils.createBlur(padre, bmImg);

                    padre.runOnUiThread(new Runnable() {
                        public void run() {
                            bgfondo.setImageBitmap(actualImg);
                            ImgUtils.ImageViewAnimatedChange(padre, bg, actualImg);
                        }
                    });

                    Thread.sleep(4000);
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


}
