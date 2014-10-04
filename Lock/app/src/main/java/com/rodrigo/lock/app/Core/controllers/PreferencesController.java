package com.rodrigo.lock.app.Core.controllers;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.util.Base64;

import com.rodrigo.lock.app.Core.Interfaces.IPreferences;
import com.rodrigo.lock.app.Core.Utils.Utils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

/**
 * Created by Rodrigo on 25/05/14.
 */
public class PreferencesController implements IPreferences {


    Context c;
    SharedPreferences prefs;
    android.content.SharedPreferences.Editor editor;



    private static boolean defaultEncryptarModoImagen = true;
    private static boolean defaultEncryptarMismaPassword= false;
  //  private static boolean defaultDesencryptarVistaSegura = true;

    protected static final String UTF8 = "utf-8";


    private PreferencesController(Context c) {
        this.c = c;
        prefs =  c.getSharedPreferences("Pref", Context.MODE_PRIVATE);
        editor =  prefs.edit();
    }

    public static IPreferences getPreferencesController(Context c) {
        return new PreferencesController(c);
    }


    //
    @Override
    public boolean isDefaultEncryptarMismaPassword() {
        return defaultEncryptarMismaPassword;
    }

    @Override
    public boolean getEncryptarMismaPassword() {
        return prefs.getBoolean("defaultEncryptarMismaPassword", defaultEncryptarMismaPassword);
    }


    @Override
    public void setEncryptarMismaPassword(boolean EncryptarMismaPassword) {
        editor.putBoolean("defaultEncryptarMismaPassword", EncryptarMismaPassword);
        editor.commit();
    }

//


/*
    @Override
    public boolean isDefaultEncryptarModoImagen() {
        return defaultEncryptarModoImagen;
    }

    @Override
    public boolean getEncryptarModoImagen() {
        boolean res =prefs.getBoolean("defaultEncryptarModoImagen", defaultEncryptarModoImagen);
        return res;
    }


    @Override
    public void setEncryptarModoImagen(boolean encryptarModoImagen) {
        editor.putBoolean("defaultEncryptarModoImagen", encryptarModoImagen);
        editor.commit();
    }
*/
//
   /* @Override
    public boolean isDefaultDesencryptarVistaSegura() {
        return defaultDesencryptarVistaSegura;
    }

    @Override
    public boolean getDesencryptarVistaSegura() {
        return prefs.getBoolean("defaultDesencryptarVistaSegura", defaultDesencryptarVistaSegura);
    }


    @Override
    public void setDesencryptarVistaSegura(boolean DesencryptarVistaSegura) {
        editor.putBoolean("defaultDesencryptarVistaSegura", DesencryptarVistaSegura);
        editor.commit();
    }*/

//

    @Override
    public void setPassword (String password) {
        editor.putString("password", encrypt(password));
        editor.commit();
    }

    @Override
    public String getPassword() {
        final String v = prefs.getString("password", null);
        return v != null ? decrypt(v) :  "";
    }







    protected String encrypt( String value ) {

        try {
            final String SEKRIT = Utils.getUniquePsuedoID();//sure safe
            final byte[] bytes = value!=null ? value.getBytes(UTF8) : new byte[0];
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
            SecretKey key = keyFactory.generateSecret(new PBEKeySpec(SEKRIT.toCharArray()));
            Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
            pbeCipher.init(Cipher.ENCRYPT_MODE, key, new PBEParameterSpec(Settings.Secure.getString(c.getContentResolver(),Settings.System.ANDROID_ID).getBytes(UTF8), 20));
            return new String(Base64.encode(pbeCipher.doFinal(bytes), Base64.NO_WRAP),UTF8);

        } catch( Exception e ) {
            throw new RuntimeException(e);
        }

    }

    protected String decrypt(String value){
        try {
            final String SEKRIT = Utils.getUniquePsuedoID();//sure safe
            final byte[] bytes = value!=null ? Base64.decode(value,Base64.DEFAULT) : new byte[0];
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
            SecretKey key = keyFactory.generateSecret(new PBEKeySpec(SEKRIT.toCharArray()));
            Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
            pbeCipher.init(Cipher.DECRYPT_MODE, key, new PBEParameterSpec(Settings.Secure.getString(c.getContentResolver(),Settings.System.ANDROID_ID).getBytes(UTF8), 20));
            return new String(pbeCipher.doFinal(bytes),UTF8);

        } catch( Exception e) {
            throw new RuntimeException(e);
        }
    }


}
