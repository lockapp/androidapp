package com.rodrigo.lock.core.crypto.AES;


import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidParameterSpecException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

/**
 * @author Rodrigo
 */
public class Crypto {


    public static CoreCrypto createCryptoV0() throws Exception{
    	  CoreCryptoV0 _c = new CoreCryptoV0();
          return _c.new AES();
    }
    

    public static CoreCrypto createCryptoV1() throws Exception{
    	CoreCryptoV1 _c = new CoreCryptoV1();
          return _c.new AES();
    }
    
    
//    public void initV0() throws Exception {
//        CoreCryptoV0 _c = new CoreCryptoV0();
//        aes = _c.new AES();
//    }
//
//    public void initV1() throws Exception {
//        CoreCryptoV1 _c = new CoreCryptoV1();
//        aes = _c.new AES();
//    }
//
//
//    public String encrypt(String text) throws Exception {
//        if (text != null) {
//            return aes.encrypt(text);
//        } else {
//            return null;
//        }
//
//    }
//    
//    public Cipher getCiphertoEnc(OutputStream out, String password) throws Exception {
//        return (aes.getCiphertoEncZip(out, password));
//    }
//
//    public Cipher getCiphertoDec(InputStream in, String password) throws Exception {
//        return (aes.getCiphertoDecZip(in, password));
//    }

}
