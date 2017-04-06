package com.rodrigo.lock.app.Core.Manejadores;


import com.rodrigo.lock.app.Core.controllers.FileController;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Rodrigo on 30/07/2014.
 */
public class ManejadorFile {
    static int count = 0;
    static Map<Integer, FileController> archivosEnTrabajo = new HashMap<Integer, FileController>();

    public static synchronized FileController createControler (){
        FileController fc = new FileController(count);
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
