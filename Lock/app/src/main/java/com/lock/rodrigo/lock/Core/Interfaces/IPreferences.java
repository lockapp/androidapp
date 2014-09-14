package com.lock.rodrigo.lock.Core.Interfaces;

/**
 * Created by Rodrigo on 25/05/14.
 */
public interface IPreferences {
    public static String extendion = "pbx";

  /*  public  boolean isDefaultEncryptarModoImagen();
    public boolean getEncryptarModoImagen();
    public void setEncryptarModoImagen(boolean encryptarModoImagen);

    public boolean isDefaultDesencryptarVistaSegura();
    public boolean getDesencryptarVistaSegura();
    public void setDesencryptarVistaSegura(boolean DesencryptarVistaSegura);
*/
    public void setPassword (String password) ;

    public String getPassword();



    public boolean isDefaultEncryptarMismaPassword();
    public boolean getEncryptarMismaPassword() ;

    public void setEncryptarMismaPassword(boolean EncryptarMismaPassword) ;
/*
    public boolean getEncryptarEliminarOriginal();

    public boolean getDesencryptarEliminarOriginal();

    public void setDesencryptarAbrirAlTerminar(boolean desencryptarAbrirAlTerminar);

    public void setDesencryptarEliminarOriginal(boolean desencryptarEliminarOriginal);

    public void setEncryptarEliminarOriginal(boolean encryptarEliminarOriginal);

    public void setEncryptarMismaPassword(boolean encryptarMismaPassword);

    public void setModoImagen(boolean modoImagen);

    public void setEncryptarPassword(String s);

    public  boolean isDefaultEncryptarEliminarOriginal();

    public  boolean isDefaultDesencryptarEliminarOriginal();


    public  boolean isDefaultDesencryptarModoImagen();



    public boolean getDesencryptarModoImagen();

*/


    }
