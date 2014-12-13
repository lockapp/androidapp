package com.rodrigo.lock.app.Core.crypto.AES;


import com.rodrigo.lock.app.Core.crypto.AES.CoreCrypto128.Utils;

import java.io.InputStream;
import java.io.OutputStream;

import javax.crypto.Cipher;

/**
 * @author Rodrigo
 */
public class Crypto {

    CoreCrypto aes;


    public void init128() throws Exception {
        CoreCrypto128 _c = new CoreCrypto128();
        aes = _c.new AES();
    }

    public void init256() throws Exception {
        CoreCrypto256 _c = new CoreCrypto256();
        aes = _c.new AES();
    }


    public String encrypt(String text) throws Exception {
        if (text != null) {
            return aes.encrypt(text);
        } else {
            return null;
        }

    }

    public Cipher getCiphertoEnc(OutputStream out, String password) throws Exception {
        return (aes.getCiphertoEncZip(out, password));
    }

    public Cipher getCiphertoDec(InputStream in, String password) throws Exception {
        return (aes.getCiphertoDecZip(in, password));
    }

}
