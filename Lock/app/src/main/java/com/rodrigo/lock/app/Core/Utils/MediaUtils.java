package com.rodrigo.lock.app.Core.Utils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;

import android.support.v8.renderscript.*;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.rodrigo.lock.app.R;

/**
 * Created by Rodrigo on 29/05/14.
 */
public class MediaUtils {
    static final int MAX_IMAGE_SIZE = 900;
    static final int SMALL_IMAGE_SIZE = 150;


    public static long isVideoInGallery(File inF,  Context ctx) {
        // Set up the projection (we only need the ID)
        long idVideo = -1;
        String[] projection = {MediaStore.Video.Media._ID};


        // Match on the file path
        String selection = MediaStore.Video.Media.DATA + " = ?";
        String[] selectionArgs = new String[]{inF.getAbsolutePath()};

        // Query for the ID of the media matching the file path
        Uri queryUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        ContentResolver contentResolver = ctx.getContentResolver();
        Cursor c = contentResolver.query(queryUri, projection, selection, selectionArgs, null);
        if (c.moveToFirst()) {
            // We found the ID. Deleting the item via the content provider will also remove the file
            idVideo = c.getLong(c.getColumnIndexOrThrow(MediaStore.Video.Media._ID));
        } else {
            // File not found in med
            // ia store DB
            idVideo = -1;
        }

        c.close();
        return idVideo;
    }


    public static long isImageInGallery(File inF,  Context ctx) {
        // Set up the projection (we only need the ID)
        long idImage = -1;
        String[] projection = {MediaStore.Images.Media._ID};


        // Match on the file path
        String selection = MediaStore.Images.Media.DATA + " = ?";
        String[] selectionArgs = new String[]{inF.getAbsolutePath()};

        // Query for the ID of the media matching the file path
        Uri queryUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        ContentResolver contentResolver = ctx.getContentResolver();
        Cursor c = contentResolver.query(queryUri, projection, selection, selectionArgs, null);
        if (c.moveToFirst()) {
            // We found the ID. Deleting the item via the content provider will also remove the file
            idImage = c.getLong(c.getColumnIndexOrThrow(MediaStore.Images.Media._ID));
        } else {
            // File not found in med
            // ia store DB
            idImage = -1;
        }

        c.close();
        return idImage;
    }







    public static void deleteImageGallery(long idImage, Context ctx) {
        Uri deleteUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, idImage);
        ContentResolver contentResolver = ctx.getContentResolver();
        contentResolver.delete(deleteUri, null, null);
    }

    public static void deleteVideoGallery(long id, Context ctx) {
        Uri deleteUri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id);
        ContentResolver contentResolver = ctx.getContentResolver();
        contentResolver.delete(deleteUri, null, null);
    }


    public static void addImageGallery(File imageFile, Context ctx) {
        /*** se agrega la actual a la galeria **/
        ContentValues image = new ContentValues();

        //image.put(MediaStore.Images.Media.TITLE, imageTitle);
        //image.put(MediaStore.Images.Media.DISPLAY_NAME, imageDisplayName);
        //image.put(MediaStore.Images.Media.DESCRIPTION, imageDescription);
        long current = System.currentTimeMillis();

        image.put(MediaStore.Images.Media.DATE_TAKEN, current);
        image.put(MediaStore.Images.Media.DATE_ADDED, (int) (current / 1000));
        image.put(MediaStore.Images.Media.DATE_MODIFIED,  (int) (current / 1000));
       // image.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
       // image.put(MediaStore.Images.Media.ORIENTATION, 0);

        File parent = imageFile.getParentFile();
        String path = parent.toString().toLowerCase();
        String name = parent.getName().toLowerCase();
        image.put(MediaStore.Images.ImageColumns.BUCKET_ID, path.hashCode());
        image.put(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME, name);
        image.put(MediaStore.Images.Media.SIZE, imageFile.length());

        image.put(MediaStore.Images.Media.DATA, imageFile.getAbsolutePath());
        Uri result = ctx.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, image);

      /*  Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(pathImage);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        ctx.sendBroadcast(mediaScanIntent);*/
    }

    public static void addVideoGallery(File videoFile ,  Context ctx) {
        ContentValues values = new ContentValues(3);
        long current = System.currentTimeMillis();
        values.put(MediaStore.Video.Media.DATE_TAKEN, current);
        values.put(MediaStore.Video.Media.DATE_ADDED, (int) (current / 1000));
        values.put(MediaStore.Video.Media.DATE_MODIFIED,  (int) (current / 1000));


        File parent = videoFile.getParentFile();
        String path = parent.toString().toLowerCase();
        String name = parent.getName().toLowerCase();
        values.put(MediaStore.Video.VideoColumns.BUCKET_ID, path.hashCode());
        values.put(MediaStore.Video.VideoColumns.BUCKET_DISPLAY_NAME, name);
        values.put(MediaStore.Video.Media.SIZE, videoFile.length());
        values.put(MediaStore.Video.Media.DATA, videoFile.getAbsolutePath());
        Uri result =  ctx.getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
    }


    public static void UpdateMediaStore(String pathFile, Context ctx) {
        /*** se agrega la actual a la galeria **/
      /*   File f = new File(pathFile);
        Uri contentUri = Uri.fromFile(f);
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,contentUri);
        ctx.sendBroadcast(mediaScanIntent);*/



/*
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(pathFile);
        Uri contentUri = Uri.fromFile(f.getParentFile());
        mediaScanIntent.setData(contentUri);
        ctx.sendBroadcast(mediaScanIntent);
  */  }





    public static Bitmap TransformImage(String path ){
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, options);
            /*int imageHeight = options.outHeight;
            int imageWidth = options.outWidth;
            String imageType = options.outMimeType;*/

            options.inSampleSize = calculateInSampleSize(options, MAX_IMAGE_SIZE, MAX_IMAGE_SIZE);
            options.inJustDecodeBounds = false;

            return BitmapFactory.decodeFile(path, options);

    }


    public static Bitmap TransformSmallImage(String path ){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
            /*int imageHeight = options.outHeight;
            int imageWidth = options.outWidth;
            String imageType = options.outMimeType;*/

        options.inSampleSize = calculateInSampleSize(options, SMALL_IMAGE_SIZE, SMALL_IMAGE_SIZE);
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(path, options);

    }



    public static int calculateInSampleSize( BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }


    public static void createBlur(Context context, Bitmap bitmapOriginal ){
        float BLUR_RADIUS = 8f;
        //define this only once if blurring multiple times
        RenderScript rs = RenderScript.create(context);
//this will blur the bitmapOriginal with a radius of 8 and save it in bitmapOriginal
        final Allocation input = Allocation.createFromBitmap(rs, bitmapOriginal); //use this constructor for best performance, because it uses USAGE_SHARED mode which reuses memory
        final Allocation output = Allocation.createTyped(rs, input.getType());
        final ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        script.setRadius(BLUR_RADIUS);
        script.setInput(input);
        script.forEach(output);
        output.copyTo(bitmapOriginal);
    }

    public static void ImageViewAnimatedChange(Context c, final ImageView v, final Bitmap new_image) {
      //  final Animation anim_out = AnimationUtils.loadAnimation(c, android.R.anim.fade_out);
      //  final Animation anim_in  = AnimationUtils.loadAnimation(c, android.R.anim.fade_in);

        final Animation anim_out = AnimationUtils.loadAnimation(c, R.anim.img_out);
       /* final Animation anim_in  = AnimationUtils.loadAnimation(c, R.anim.img_in);*/
        anim_out.setAnimationListener(new Animation.AnimationListener()
        {
            @Override public void onAnimationStart(Animation animation) {}
            @Override public void onAnimationRepeat(Animation animation) {}
            @Override public void onAnimationEnd(Animation animation)
            {
                v.setImageBitmap(new_image);
             /*   anim_in.setAnimationListener(new Animation.AnimationListener() {
                    @Override public void onAnimationStart(Animation animation) {}
                    @Override public void onAnimationRepeat(Animation animation) {}
                    @Override public void onAnimationEnd(Animation animation) {}
                });
                v.startAnimation(anim_in);*/
            }
        });
        v.startAnimation(anim_out);
    }





    public static void ImageViewAnimatedChangeComplete(Context c, final ImageView v, final int new_image) {
        final Animation anim_out = AnimationUtils.loadAnimation(c, R.anim.img_out);
        final Animation anim_in  = AnimationUtils.loadAnimation(c, R.anim.img_in);
        anim_out.setAnimationListener(new Animation.AnimationListener()
        {
            @Override public void onAnimationStart(Animation animation) {}
            @Override public void onAnimationRepeat(Animation animation) {}
            @Override public void onAnimationEnd(Animation animation)
            {
                v.setImageResource(new_image);
                anim_in.setAnimationListener(new Animation.AnimationListener() {
                    @Override public void onAnimationStart(Animation animation) {}
                    @Override public void onAnimationRepeat(Animation animation) {}
                    @Override public void onAnimationEnd(Animation animation) {}
                });
                v.startAnimation(anim_in);
            }
        });
        v.startAnimation(anim_out);
    }
}
