package com.rodrigo.lock.app.Core.controllers.crypto;

import android.content.Context;

import com.rodrigo.lock.app.Core.Clases.Accion;
import com.rodrigo.lock.app.Core.Clases.FileHeader;
import com.rodrigo.lock.app.Core.controllers.FileController;
import com.rodrigo.lock.app.services.ExtractService;

/**
 * Created by Rodrigo on 13/08/2014.
 */
public class CryptoController {
    Context ctx;
    ExtractService SM;
    Accion accion;

    //FileController fc;
    FileHeader cabezal;
    String pass;


    public void realizarTrabajo(ExtractService SM, int idN)  throws Exception {
    }

    public  void checkAndInit() throws Exception{
    }


    public void setContext(Context c){
        this.ctx= c;
    }

    public Accion getAccion() {
        return accion;
    }
}
