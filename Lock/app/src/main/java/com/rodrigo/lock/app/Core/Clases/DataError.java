package com.rodrigo.lock.app.Core.Clases;

/**
 * Created by Rodrigo on 05/12/2014.
 */
public class DataError {
    public enum ERROR {ERROR_PASSWORD, ERROR_VENCIMIENTO, ERROR_VERSION, ERROR_EXTRACT, ERROR_OTRO }

    ERROR error;
    String descripcion;

    public DataError(ERROR error, String descripcion){
        this.error= error;
        this.descripcion=descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
