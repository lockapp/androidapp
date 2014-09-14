package com.rodrigo.lock.app.Core.crypto;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.util.Log;

import com.rodrigo.lock.app.Core.Controladores.FileController;
import com.rodrigo.lock.app.Core.Utils.ImgUtils;
import com.rodrigo.lock.app.R;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by Rodrigo on 11/07/2014.
 */
public class DecryptControllerSeeImage extends DecryptController {
    boolean complete;
    private LruCache<String, Bitmap> mMemoryCache;
    private int idC;
    private int cantImages = 0;
    File cacheDirectory;


    public int getCantImages(){
        return cantImages;
    }


    public boolean isComplete() {
        return complete;
    }

    public DecryptControllerSeeImage(FileController fc, Context c) throws Exception {
        super(fc, c);

        this.idC =fc.getId();
        this.complete = false;
        this.vistaSegura = true;

        ContextWrapper cw = new ContextWrapper(c);
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


/*

    public void initImages()throws Exception{
        initImage();
        openCabezales();

        Crypto algo = new Crypto();
        algo.init(pass);

        in = new CipherInputStream(in, algo.getCiphertoDec(in));
        input = new ZipInputStream(in);

        ze = input.getNextEntry();
        //chequea si desbloqueo
        if (ze == null) {
            throw new Exception(this.c.getResources().getString(R.string.error_password));
        }


    }
*/


    public synchronized void loadImage() throws Exception {
        if (!this.complete) {
            try {
                if (ze == null) {
                    this.complete = true;
                    input.closeEntry();
                    input.close();
                    //in.close();
                } else {
                    byte[] buffer = new byte[1024];
                    String fileName = ze.getName();

                    File newFile = new File(cacheDirectory, idC + "-" + cantImages + ".tmp");
                    FileOutputStream faos = new FileOutputStream(newFile);
                    int len;
                    while ((len = input.read(buffer)) > -1) {
                        faos.write(buffer, 0, len);
                    }
                    faos.close();

                    cantImages++;
                    ze = input.getNextEntry();
                }

            } catch (Exception e) {
                throw new Exception(String.format(c.getResources().getString(R.string.error_open), inFile, e.getMessage()));
            }
        }
    }

    public  Bitmap getImage(int idI){
        try {
            String imageKey = idC +"-"+idI +".tmp";
            Bitmap bitmap = mMemoryCache.get(imageKey);

            if (bitmap == null) {
                File f=new File(cacheDirectory.getAbsolutePath(), imageKey);
                bitmap = ImgUtils.TransformImage(f.getAbsolutePath());
                mMemoryCache.put(imageKey, bitmap);
            }/*else{
                hit ++;
            }
            Log.d("-------------------- > cahce hist / miss ", hit +" / " + miss);*/

            return bitmap;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }



    public void delteCache(){
        for (int idI = 0; idI <cantImages; idI++){
            String imageKey = idC +"-"+idI +".tmp";
            File f=new File(cacheDirectory.getAbsolutePath(), imageKey);
            f.delete();
            Log.d("-------------------- > archivo eliminado", imageKey);
        }
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
