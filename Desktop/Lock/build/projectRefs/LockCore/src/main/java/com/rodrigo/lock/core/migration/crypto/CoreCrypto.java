package com.rodrigo.lock.core.migration.crypto;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;

/**
 * Created by Rodrigo on 12/12/2014.
 */
public interface CoreCrypto {
    public Cipher getCiphertoEncZip(OutputStream out, String password) throws Exception;
    public Cipher getCiphertoDecZip(InputStream in, String password) throws Exception ;
    public String encrypt(String plaintext)
            throws NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException,
            InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException;

}
