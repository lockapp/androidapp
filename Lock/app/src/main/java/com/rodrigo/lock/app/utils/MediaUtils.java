package com.rodrigo.lock.app.utils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;

import com.rodrigo.lock.app.LockApplication;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;

/**
 * Created by Rodrigo on 07/12/2016.
 */

public class MediaUtils {

    public static final int SMALL_IMAGE_SIZE = 128;

    public static boolean rename(File from, File to) {
        return from.getParentFile().exists() && from.exists() && from.renameTo(to);
    }

    public static boolean isExtensionImage(String extension) {
        return (extension.equals("jpg") || extension.equals("jpeg") || extension.equals("jpe") || extension.equals("bmp") || extension.equals("webp")
                || extension.equals("png")
                || extension.equals("gif"));
    }

    public static boolean isExtensionVideo(String extension) {
        return (extension.equals("3gp") || extension.equals("mp4") || extension.equals("ts") || extension.equals("webm") || extension.equals("mkv") );
    }



    public static InputStream bitMapToInputStream(Bitmap bitmap){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return new ByteArrayInputStream(stream.toByteArray());
    }


    public static Bitmap getVideoPreview (String path){
        return  ThumbnailUtils.createVideoThumbnail(path, MediaStore.Images.Thumbnails.MINI_KIND);
    }


    public static Bitmap getImagePreview(String path ){
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



    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
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



    public static void addImageToGallery(File imageFile) {
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
        Uri result = LockApplication.getAppContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, image);

      /*  Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(pathImage);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        ctx.sendBroadcast(mediaScanIntent);*/
    }

    public static void addVideoToGallery(File videoFile ) {
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
        Uri result =  LockApplication.getAppContext().getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
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



}
