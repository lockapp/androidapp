package com.lock.rodrigo.lock.Core.Manejadores;

import android.content.Context;

import com.lock.rodrigo.lock.Core.Controladores.FileController;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Rodrigo on 30/07/2014.
 */
public class ManejadorFile {
    static int count = 0;
    static Map<Integer, FileController> archivosEnTrabajo = new HashMap<Integer, FileController>();

    public static FileController createControler (Context c){
        FileController fc = new FileController(count, c);
        archivosEnTrabajo.put(count, fc);
        count++;
        return fc;
    }

    public  static FileController getControlador (int id){
        return archivosEnTrabajo.get(id);
    }

    public static void quitarControldor(int id){
        archivosEnTrabajo.remove(id);
    }

}
