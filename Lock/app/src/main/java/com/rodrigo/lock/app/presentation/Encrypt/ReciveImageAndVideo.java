package com.rodrigo.lock.app.presentation.Encrypt;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.rodrigo.lock.app.Constants;
import com.rodrigo.lock.app.Core.Clases.Accion;
import com.rodrigo.lock.app.Core.Clases.Archivo;
import com.rodrigo.lock.app.Core.Clases.FileType;
import com.rodrigo.lock.app.Core.Utils.MediaUtils;
import com.rodrigo.lock.app.R;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Rodrigo on 27/09/2014.
 */
public class ReciveImageAndVideo extends ReceiveAndEncryptActivity{

    private final static int INTERVAL_UPDATEBG = 1000 * 6  ; //4 segundos

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
/*
    @Override
    public void finalizar(){
        Intent intent = new Intent(this, ReciveImageAndVideo.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        intent.putExtra(Constants.FINISH, true);
        startActivity(intent);
    }*/

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





/*****************************************************************************/
/**  actualizador de imagenes **/
/*****************************************************************************/

    @Override
    protected void onStop(){
        super.onStop();
        if (controler.getAccion() == Accion.EncryptarConImagen){
            stopRepeatingTask();
        }
    }

    @Override
    protected void onStart(){
        super.onStart();

        if (controler.getAccion() == Accion.EncryptarConImagen){
            startRepeatingTask();
        }

    }


    void startRepeatingTask(){
        mHandlerTask =new Runnable()
        {
            @Override
            public void run() {
                imgIter = imgIter % controler.getInFiles().size();
                boolean res = generarProximaIagen();
                actualizarUI(res);

                if (controler.getInFiles().size() > 1){
                    imgIter++;
                    mHandler.postDelayed(mHandlerTask, INTERVAL_UPDATEBG);
                }

            }
        };
        mHandlerTask.run();
    }

    void stopRepeatingTask() {
        if (controler.getInFiles().size() > 1){
            mHandler.removeCallbacks(mHandlerTask);
            mHandlerTask= null;
        }
    }




    int imgIter =0;
    Runnable mHandlerTask = null;



    Archivo actual;
    Bitmap actualImg;


    private Boolean generarProximaIagen() {
        try {
            actual = controler.getInFiles().get(imgIter);
            if (actual.getTipo() == FileType.Imagen){
                actualImg = MediaUtils.TransformImage(actual.getFile().getAbsolutePath());
            } else   if (actual.getTipo() == FileType.Video){
                actualImg = ThumbnailUtils.createVideoThumbnail(actual.getFile().getAbsolutePath(), MediaStore.Images.Thumbnails.MINI_KIND);
            }
            return true;

        } catch (Exception e) {
            Log.d("actualizador imagenes", "exepcion al generar nueva imagen");
            return false;
        }
    }

    private void actualizarUI(Boolean result) {
        try {
            if (result == true) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                  //      if (actual.getTipo() == FileType.Imagen) {
                            //bgfondo.setImageBitmap(actualImg);
                        bg.setVisibility(View.VISIBLE);

                        MediaUtils.ImageViewAnimatedChange(ReciveImageAndVideo.this, bg, actualImg);

                            //  videoVew.stop();
                            //bgfondo.setVisibility(View.VISIBLE);
                            //bg.setVisibility(View.VISIBLE);
                            //  videoVew.setVisibility(View.GONE);

    /*                    } else {

                                videoVew.setScaleType(TextureVideoView.ScaleType.TOP);
                                videoVew.setDataSource(actual.getFile().getAbsolutePath());
                                videoVew.setLooping(true);
                                // videoVew.mute();
                                bg.setVisibility(View.GONE);
                                bgfondo.setVisibility(View.GONE);
                                videoVew.setVisibility(View.VISIBLE);

                                videoVew.play();
                        }*/
                    }
                });
            }
        } catch (Exception e) {
            Log.d("actualizador imagenes", "dio exepcion al acctualizar");

        }

    }




}
