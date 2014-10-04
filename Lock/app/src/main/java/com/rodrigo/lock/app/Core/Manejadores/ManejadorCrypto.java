package com.rodrigo.lock.app.Core.Manejadores;

import com.rodrigo.lock.app.Core.controllers.crypto.CryptoController;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Rodrigo on 02/10/2014.
 */
public class ManejadorCrypto {
    static int count = 0;
    static Map<Integer, CryptoController> archivosEnTrabajo = new HashMap<Integer, CryptoController>();

    public static int add (CryptoController c){
        count++;
        archivosEnTrabajo.put(count, c);
        return count;
    }

    public  static CryptoController getControlador (int id){
        return archivosEnTrabajo.get(id);
    }

    public static void quitarControldor(int id){
        archivosEnTrabajo.remove(id);
    }



}
