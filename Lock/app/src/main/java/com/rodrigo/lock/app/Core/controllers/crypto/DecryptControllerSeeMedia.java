package com.rodrigo.lock.app.Core.controllers.crypto;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.util.Log;

import com.rodrigo.lock.app.Core.Clases.Accion;
import com.rodrigo.lock.app.Core.Clases.Archivo;
import com.rodrigo.lock.app.Core.Clases.FileType;
import com.rodrigo.lock.app.Core.controllers.FileController;
import com.rodrigo.lock.app.Core.Interfaces.NotifyMediaChange;
import com.rodrigo.lock.app.Core.Utils.MediaUtils;
import com.rodrigo.lock.app.R;

import java.io.File;
import java.io.FileOutputStream;
import java.util.LinkedList;

/**
 * Created by Rodrigo on 11/07/2014.
 */
public class DecryptControllerSeeMedia extends DecryptController implements  NotifyMediaChange{
    boolean complete;
    private LruCache<String, Bitmap> mMemoryCache;
    private int idC;
    private int cantImages = 0;
    File cacheDirectory;
    LinkedList <Archivo> abiertos = new LinkedList<Archivo>();
    boolean salir = false;
    NotifyMediaChange notificarCambio=null;
    private final Object lock = new Object();

    public LinkedList<Archivo> getAbiertos() {
        return abiertos;
    }

    public DecryptController getDecryptController(Context c)  {
        return new DecryptController ( c,  inFile,  pass,  accion,  offset);
    }



    public int getCantImages(){
        return cantImages;
    }


    public boolean isComplete() {
        return complete;
    }

    public DecryptControllerSeeMedia(int id, Context ctx, File inFile, String pass, Accion accio, long offset) throws Exception {
        super(ctx, inFile, pass, accio, offset);

        this.idC =id;
        this.complete = false;
        this.vistaSegura = true;

        ContextWrapper cw = new ContextWrapper(ctx);
        cacheDirectory = cw.getDir("imageDir", Context.MODE_PRIVATE);


        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 6;
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount() / 1024;
            }
        };

    }



    public /*synchronized*/ void loadImage() throws Exception {
        if (!this.complete) {
            try {
                if (ze == null) {
                    this.complete = true;
                    input.closeEntry();
                    input.close();
                    fin();
                    //in.close();
                } else {
                    byte[] buffer = new byte[16384];
                    String fileName = ze.getName();


                   // File newFile = new File(ctx.getFilesDir(), idC + "-" + cantImages + "-"+ fileName);
                   // FileOutputStream outputStream = ctx.openFileOutput("myfile.mp4", Context.MODE_WORLD_READABLE);

                    File newFile = new File(cacheDirectory, idC + "-" + cantImages + "-"+ fileName);
                    FileOutputStream faos = new FileOutputStream(newFile);


                    int len;
                    while (((len = input.read(buffer)) > -1)&&(!salir)) {
                        faos.write(buffer, 0, len);
                    }
                    faos.close();
                    //faos.close();

                    if (salir){
                        ze = null;
                        newFile.delete();
                    }else {
                        abiertos.add( new Archivo(newFile));
                        cantImages++;
                        notificarCantImages(cantImages);
                        ze = input.getNextEntry();
                    }
                }

            } catch (Exception e) {
                throw new Exception(String.format(ctx.getResources().getString(R.string.error_open), inFile, e.getMessage()));
            }
        }
    }


    public FileType getFileType (int idF){
        return abiertos.get(idF).getTipo();
    }


    public File getFile (int idF){
        return abiertos.get(idF).getFile();
    }


    public  Bitmap getImage(int idI){
        try {
            String imageKey =  "big:" + idI;
            Bitmap bitmap = mMemoryCache.get(imageKey);

            if (bitmap == null) {
                bitmap = MediaUtils.TransformImage( abiertos.get(idI).getFile().getAbsolutePath());
                mMemoryCache.put(imageKey, bitmap);
            }

            return bitmap;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }


    public  Bitmap getSmallImage(int idI){
        try {
            String imageKey =  "small:" + idI;
            Bitmap bitmap = mMemoryCache.get(imageKey);

            if (bitmap == null) {
                bitmap = MediaUtils.TransformSmallImage( abiertos.get(idI).getFile().getAbsolutePath());
                mMemoryCache.put(imageKey, bitmap);
            }

            return bitmap;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }



    public void delteCache(){
        for (Archivo a : abiertos){
            String imageKey = a.getFile().getAbsolutePath();
            //File f=new File(cacheDirectory.getAbsolutePath(), imageKey);
            a.getFile().delete();
            Log.d("-------------------- > archivo eliminado", imageKey);
        }
    }

    @Override
    public void notificarCantImages(int cantImages) {
        synchronized (lock) {
            if(notificarCambio!=null  && !salir)
                this.notificarCambio.notificarCantImages(cantImages);
        }
    }

    @Override
    public void fin() {
        synchronized (lock) {
            if(notificarCambio!=null && !salir)
                this.notificarCambio.fin();
        }
    }


    public void setNotificarCambio(NotifyMediaChange notificarCambio) {
        synchronized (lock) {
            this.notificarCambio = notificarCambio;
        }
    }


    public boolean isSalir() {
        return salir;
    }

    public void setSalir(boolean salir) {
            this.salir = salir;
    }



/*
    private void addImage(ByteArrayOutputStream baos) throws Exception {

        InputStream is1 = new ByteArrayInputStream(baos.toByteArray());
        InputStream is2 = new ByteArrayInputStream(baos.toByteArray());

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(is1, null, options);
        int imageHeight = options.outHeight;
        int imageWidth = options.outWidth;
        String imageType = options.outMimeType;

        options.inSampleSize = ImgUtils.calculateInSampleSize(options, anchoPantalla, largoPantalla);
        options.inJustDecodeBounds = false;

        Bitmap bitmap = BitmapFactory.decodeStream(is2, null, options);

        imagenes.add(bitmap);
        cantImages++;
    }
*/

}
