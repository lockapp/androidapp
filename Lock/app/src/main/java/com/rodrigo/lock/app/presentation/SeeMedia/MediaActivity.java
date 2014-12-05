package com.rodrigo.lock.app.presentation.SeeMedia;

import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;

import com.devspark.appmsg.AppMsg;
import com.rodrigo.lock.app.Core.Interfaces.NotifyMediaChange;
import com.rodrigo.lock.app.Core.Manejadores.ManejadorCrypto;
import com.rodrigo.lock.app.Core.Utils.Utils;
import com.rodrigo.lock.app.Core.controllers.crypto.CryptoController;
import com.rodrigo.lock.app.Core.controllers.crypto.DecryptControllerSeeMedia;
import com.rodrigo.lock.app.R;
import com.rodrigo.lock.app.services.ExtractService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rodrigo on 02/10/2014.
 */
public class MediaActivity extends ActionBarActivity implements NotifyMediaChange {

    boolean clearCacheFiles = true;
    boolean deleteMediaController = true;
    int idCC;

    DecryptControllerSeeMedia mediaCryptoController;
   // FileController fc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //se busca el decript controler
        idCC = getIntent().getExtras().getInt("controlerId");
        mediaCryptoController = (DecryptControllerSeeMedia)ManejadorCrypto.getControlador(idCC);
    }


    @Override
    public synchronized void notificarCantImages(int cantImages) {
    }

    @Override
    public synchronized void fin() {
    }

    public void mostrarError(String error){
        AppMsg appMsg = AppMsg.makeText(this, error,  AppMsg.STYLE_ALERT);
        appMsg.setDuration(8000);
        appMsg.show();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        if (isFinishing()) {
            if (clearCacheFiles){
                mediaCryptoController.setSalir(true);
                mediaCryptoController.delteCache();
            }
            if (deleteMediaController) ManejadorCrypto.quitarControldor(idCC);
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        mediaCryptoController.setNotificarCambio(this);

    }


    public void setClearCacheFiles(boolean clearCacheFiles) {
        this.clearCacheFiles = clearCacheFiles;
    }

    public void setDeleteMediaController(boolean deleteMediaController) {
        this.deleteMediaController = deleteMediaController;
    }


    public int getIdCC() {
        return idCC;
    }




    ///menu compartir
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_addtogallery) {
            try {
                clearCacheFiles = true;
                deleteMediaController =true;

                CryptoController cc = mediaCryptoController.getDecryptController(this.getApplicationContext());
                int ccid = ManejadorCrypto.add(cc);
                Intent i = new Intent(getApplicationContext(), ExtractService.class);
                i.putExtra("controlerId", ccid);
                startService(i);

                finish();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return true;

        }else if (id ==R.id.action_share){
            Intent sendIntent = Utils.shareExludingApp(this, Uri.fromFile(mediaCryptoController.getInFile()));
            startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.send_to)));
            return true;
        }else if (id == R.id.action_donar){
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=7R9PXAXWHZ8HU"));
            startActivity(browserIntent);
            return true;
        }else{
            return super.onOptionsItemSelected(item);
        }
    }
















}
