package com.rodrigo.lock.app.mvp.addToVault;

import android.content.Intent;
import android.net.Uri;

import com.rodrigo.lock.app.migracion.MigrarUtilsDeprecated;
import com.rodrigo.lock.app.utils.UriUtils;
import com.rodrigo.lock.core.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Rodrigo on 04/12/2016.
 */

public class ResolverAccionUtils {
    public static void  resolverAccion(Intent mIntent, AddTovaultContract.irLuegoDeRecibir view) {
        try{
            List<String> archivos = Collections.EMPTY_LIST;

            String action = mIntent.getAction();
            String type = mIntent.getType();

            if (Intent.ACTION_SEND.equals(action) && type != null)
                archivos =handle1File(mIntent, view);
            else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null)
                archivos =handleMultipleFile(mIntent, view); // Handle multiple images being sent

            resolverAccion(archivos, view);
        }catch (Exception e){

        }
    }


    private static List<String>  handleMultipleFile(final Intent intent, AddTovaultContract.irLuegoDeRecibir view) {
        List<String> archivos = new LinkedList<>();
        ArrayList<Uri> uris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
        for (Uri uri : uris) {
            if (UriUtils.isLocalUri(uri)) {
                archivos.add(handleUri(uri));
            } else {
                view.mostrarArchivoNoEncontrado(String.valueOf(uri));
                throw  new RuntimeException();
                //FileNotFound(uri);
            }
        }
        return archivos;
        //resolverAccion();

    }


    private static List<String>   handle1File(final Intent intent, AddTovaultContract.irLuegoDeRecibir view) {
        List<String> archivos = new LinkedList<>();
        try {
            Uri uri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
            if (UriUtils.isLocalUri(uri)) {
                archivos.add(handleUri(uri));
            } else {
                view.mostrarArchivoNoEncontrado(String.valueOf(uri));
                throw  new RuntimeException();
                //FileNotFound(uri);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return archivos;
    }


    private static String handleUri(final Uri data) {
        String path = UriUtils.getPath( data);
        return path;
        //File archivo = new File (path);
        //archivos.add(path);
    }

    public static boolean resolverAccion(List<String> archivos, AddTovaultContract.irLuegoDeRecibir view) {
        if (archivos== null || archivos.isEmpty()){
            view.mostrarArchivoNoEncontrado("");
            return false;
        }
        for (String path: archivos){
            if (!(new File (path).exists())){
                view.mostrarArchivoNoEncontrado(path);
                return false;
            }
        }

        if (archivos.size() ==1){
            MigrarUtilsDeprecated mu = new MigrarUtilsDeprecated(archivos.get(0));
            if (mu.esEncriptadoViejo()) {
                view.irAMigrar(mu);
                return true;
            }else if (FileUtils.esBobeda(new File(archivos.get(0)))){
                view.irADecrypt(archivos.get(0));
                return true;
            }
        }
        view.irAEncrypt(new ArrayList<String>(archivos));
        return true;
    }
}
