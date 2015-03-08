package com.rodrigo.lock.app.presentation.SeeMedia;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.MenuItem;

import com.devspark.appmsg.AppMsg;
import com.rodrigo.lock.app.Constants;
import com.rodrigo.lock.app.Core.Interfaces.NotifyMediaChange;
import com.rodrigo.lock.app.Core.Manejadores.ManejadorCrypto;
import com.rodrigo.lock.app.Core.Utils.Utils;
import com.rodrigo.lock.app.Core.controllers.crypto.CryptoController;
import com.rodrigo.lock.app.Core.controllers.crypto.DecryptControllerSeeMedia;
import com.rodrigo.lock.app.R;
import com.rodrigo.lock.app.presentation.DecryptActivity;
import com.rodrigo.lock.app.services.ExtractService;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Rodrigo on 02/10/2014.
 */
public class MediaActivity extends ActionBarActivity implements NotifyMediaChange {

    boolean FLAG_IS_FINISH_TASK = true;
    int idCC;
    DecryptControllerSeeMedia mediaCryptoController;

    private static Timer backgroundTimer;
    private static TimerTask backgroundTimerTask;
    private static boolean wasInBackground;
    private static final long MAX_BACKGROUND_TIME_MS = 2*60*1000;


    public void startBackgroundTimer() {
        this.backgroundTimer = new Timer();
        this.backgroundTimerTask = new TimerTask() {
            public void run() {
                wasInBackground = true;
                exitTask();
            }
        };
        backgroundTimer.schedule(backgroundTimerTask, MAX_BACKGROUND_TIME_MS);
    }

    public void stopBackgroundTimer() {
        if (this.backgroundTimerTask != null) {
            this.backgroundTimerTask.cancel();
        }
        if (this.backgroundTimer != null) {
            this.backgroundTimer.cancel();
        }
        this.wasInBackground = false;
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //se busca el decript controler
        idCC = getIntent().getExtras().getInt(Constants.CRYPTO_CONTROLLER);
        mediaCryptoController = (DecryptControllerSeeMedia)ManejadorCrypto.getControlador(idCC);
    }




    @Override
    protected void onResume() {
        super.onResume();
        mediaCryptoController.setNotificarCambio(this);
        /*if (wasInBackground){
        }*/
        stopBackgroundTimer();

    }



    @Override
    protected void onPause(){
        super.onPause();
        startBackgroundTimer();
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


    public void exitTask(){
        //Log.d("------>", "sale y limpia todo");
        mediaCryptoController.setSalir(true);
        mediaCryptoController.delteCache();
        ManejadorCrypto.quitarControldor(idCC);
        //finish();
        Intent intent = new Intent(this, DecryptActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        intent.putExtra(Constants.FINISH, true);
        startActivity(intent);
    }


    @Override
    protected void onUserLeaveHint() {
       // Log.d("------>", "onUserLeaveHint");
        super.onUserLeaveHint();
        if (FLAG_IS_FINISH_TASK){
            stopBackgroundTimer();
            exitTask();
        }
        FLAG_IS_FINISH_TASK = true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            FLAG_IS_FINISH_TASK = false;
            finish();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }



    ///menu compartir
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_addtogallery) {
            try {
                CryptoController cc = mediaCryptoController.getDecryptController(this.getApplicationContext());
                int ccid = ManejadorCrypto.add(cc);
                Intent i = new Intent(getApplicationContext(), ExtractService.class);
                i.putExtra(Constants.CRYPTO_CONTROLLER, ccid);
                startService(i);

                exitTask();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return true;

        }else if (id ==R.id.action_share){
            exitTask();

            Intent sendIntent = Utils.shareExludingApp(this, Uri.fromFile(mediaCryptoController.getInFile()));
            startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.send_to)));
            return true;

        }else if (id == R.id.action_donar){
            exitTask();

            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=7R9PXAXWHZ8HU"));
            startActivity(browserIntent);

            return true;
        }else{
            return super.onOptionsItemSelected(item);
        }
    }



    public void setFlagIsFinishTask(boolean FLAG_IS_FINISH){
        this.FLAG_IS_FINISH_TASK = FLAG_IS_FINISH ;
    }



    public int getIdCC() {
        return idCC;
    }




}
