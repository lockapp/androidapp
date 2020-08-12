package com.rodrigo.lock.core.Utils;



import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import com.rodrigo.lock.core.enums.FileType;



/**
 * Created by Rodrigo on 07/03/2015.
 */
public class FileUtils {
    public static final byte[] PANDORABOX = hexStringToByteArray("50414e444f5241424f58");
    public static final String ENC_EXTENSION = "pbx";
    public static final String ENC_EXTENSION_2 = "etf";

    public static FileType getFileType(File file){
        String extension = FileUtils.getExtensionFile(file.getName());
        if (file.isDirectory()){
        	return FileType.Carpeta;
        }else if(FileUtils.isEncExtension(extension)){
            return FileType.OpenPBX;
        }else if(FileUtils.isEncExtension2(extension)){
            return FileType.OpenPBX_V2;
        } else if (FileUtils.isExtensionImage(extension)){
            return FileType.Imagen;
        } else if (FileUtils.isExtensionVideo(extension)){
            return FileType.Video;
        } else {
            return FileType.Otro;
        }
    }
        

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

    public  static boolean isEncExtension2(String extension) {
        return  extension.equals(ENC_EXTENSION_2);
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

        String iter = "";
        String newF = path + name + extension;
        
        while (new File(path + name + iter + extension).exists()){
            i++;
            iter = " ("+ i + ")";
        }

        return path + name + iter + extension;
    }
    

    
    public static long getSize(List<File> inFileList){
    	long size = 0;
    	for (File a : inFileList){
    		size += getSize(a);
    	}
    	return size;
    }    
    
    
    public static long getSize(File file) {
    	if (file.isDirectory()){
			long length = 0;
	        for (File iter : file.listFiles()) {
	        	length += getSize(iter);
	        }
	        return length;
    	}else{
    		return file.length();
    	}
    }
    
    
    public static String sizeToString(long tamF) {
        DecimalFormat formatter = new DecimalFormat("#########.##");
        String fileSize = String.valueOf(tamF) + " B";
        

        double aux = tamF;
        aux = aux / 1024;

        if (aux > 1) {
            fileSize = String.valueOf(formatter.format(aux)) + " kB";
        }
        aux = aux / 1024;
        if (aux > 1) {
            fileSize = String.valueOf(formatter.format(aux)) + " MB";
        }
        aux = aux / 1024;
        if (aux > 1) {
            fileSize = String.valueOf(formatter.format(aux)) + " GB";
        }
        return fileSize;
    }

    
    
    
    public static int countFiles(List<File> inFileList){
    	return inFileList.size();
//    	int count = 0;   
//    	for (File a : inFileList){
//    		count = count + countFiles(a);
//    	}
//    	return count;   	

    }

    public static int countFiles(File f){
    	if (f.isDirectory()){
    		return f.listFiles().length;
    	}else{
    		return 1;
    	}
    }
    
    
    public static String createNameForFile(){
    	String name = "lockFile";//inF.getName().replace(".", "");

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hhmm ");
        String actualDate = df.format(c.getTime());
        name= actualDate + name;
        
        return name;

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
