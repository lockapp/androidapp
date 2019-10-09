package com.rodrigo.lock.app.old.Core.Utils;

import android.net.Uri;
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by Rodrigo on 07/03/2015.
 */
public class FileUtils {
    public static final byte[] PANDORABOX = hexStringToByteArray("50414e444f5241424f58");
    public static final String ENC_EXTENSION = "pbx";


    public static String getExtensionFile(String fileName) {
        String extension = "";

        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            extension = fileName.substring(i + 1);
        }
        return extension.toLowerCase();
    }

    public static String removeExtensionFile(String fileName) {
        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            return fileName.substring(0,i );
        } else {
            return fileName;
        }

    }


    public static boolean isExtensionImage(String extension) {
        return (extension.equals("jpg") || extension.equals("jpeg") || extension.equals("jpe") || extension.equals("bmp") || extension.equals("webp")
                || extension.equals("png")
                || extension.equals("gif"));
    }

    public static boolean isExtensionVideo(String extension) {
        return (extension.equals("3gp") || extension.equals("mp4") || extension.equals("ts") || extension.equals("webm") || extension.equals("mkv") );
    }


    public  static boolean isEncExtension(String extension) {
        return  extension.equals(ENC_EXTENSION);
    }


    public static boolean isLocalUri(Uri url) {
        if (url != null){
            if( !url.toString().startsWith("http://") && !url.toString().startsWith("https://")) {
                return true;
            }
        }
        return false;
    }



    public static void delete(File file) throws IOException {

        if(file.isDirectory()){

            //directory is empty, then delete it
            if(file.list().length==0){

                file.delete();
                //  System.out.println("Directory is deleted : "  + file.getAbsolutePath());

            }else{

                //list all the directory contents
                String files[] = file.list();

                for (String temp : files) {
                    //construct the file structure
                    File fileDelete = new File(file, temp);

                    //recursive delete
                    delete(fileDelete);
                }

                //checkAndInit the directory again, if empty then delete it
                if(file.list().length==0){
                    file.delete();
                    //  System.out.println("Directory is deleted : " + file.getAbsolutePath());
                }
            }

        }else{
            //if file, then delete it
            file.delete();
            //   System.out.println("File is deleted : " + file.getAbsolutePath());
        }
    }



    public static String createNewFileNameInPath (String path, String name, String extension){
        int i=0;

        if(!TextUtils.isEmpty(extension)){
            extension= "."+extension;
        }

        String newF = path + name + extension;

        while (new File(newF).exists()){
            i++;
            newF=path + name + "("+ i + ")" + extension;
        }

        return newF;
    }




    /////private

    private static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }



    public static int byteArrayToInt(byte[] b)
    {
        return   b[3] & 0xFF |
                (b[2] & 0xFF) << 8 |
                (b[1] & 0xFF) << 16 |
                (b[0] & 0xFF) << 24;
    }

    public static byte[] intToByteArray(int a)
    {
        return new byte[] {
                (byte) ((a >> 24) & 0xFF),
                (byte) ((a >> 16) & 0xFF),
                (byte) ((a >> 8) & 0xFF),
                (byte) (a & 0xFF)
        };
    }



}
