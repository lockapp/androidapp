package com.rodrigo.lock.app.Core.controllers.crypto;

import java.util.ResourceBundle;


import com.rodrigo.lock.app.Core.Clases.Accion;
import com.rodrigo.lock.app.Core.Clases.FileHeader;

/**
 * Created by Rodrigo on 13/08/2014.
 */
public class CryptoController {
    ResourceBundle bundle;
	
    Accion accion;
    String name;
    
    //FileController fc;
    FileHeader cabezal;
    String pass;


    public void realizarTrabajo()  throws Exception {
    }

    public  void checkAndInit() throws Exception{
    }


    public void setContext(){
    	bundle = ResourceBundle.getBundle("language.lang");
    }

    public Accion getAccion() {
        return accion;
    }


    public String getName() {
        return name;
    }
}
