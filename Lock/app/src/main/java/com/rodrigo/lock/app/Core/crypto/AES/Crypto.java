package com.rodrigo.lock.app.Core.crypto.AES;


import com.rodrigo.lock.app.Core.crypto.AES.CoreCrypto.Utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import javax.crypto.Cipher;

/**
 * @author Rodrigo
 */
public class Crypto {

    CoreCrypto _c;
    CoreCrypto.AES aes;
    private byte[] salt = {
            (byte) 0xc7, (byte) 0x73, (byte) 0x21, (byte) 0x8c,
            (byte) 0x7e, (byte) 0xc8, (byte) 0xee, (byte) 0x99
    };
    private String key;

    public void init(String password) throws Exception {
        _c = new CoreCrypto();
        aes = _c.new AES();
        this.key = Utils.pbkdf2(password, Utils.byteArrayToHexString(salt), 1000, CoreCrypto.KEY_SIZE_BYTES);
    }

    public String encrypt(String text) throws Exception {
        if (text != null) {
            return aes.encrypt(text, key);
        } else {
            return null;
        }

    }

    public String decrypt(String myEncryptedText) throws Exception {
        try {
            if (myEncryptedText != null) {
                return aes.decrypt(myEncryptedText, key);
            } else {
                return null;
            }
        } catch (Exception ex) {
            throw new Exception("Tal vez la contraseña es incorrecta");
        }
    }

    public void decryptFile(String pathEncriptFile, String pathToDecryptFile) throws Exception {
        aes.decrypt(new FileInputStream(pathEncriptFile), new FileOutputStream(pathToDecryptFile), key);
    }

    public void encryptFile(String pathDecryptFile, String pathToEncrypt) throws Exception {
        aes.encrypt(new FileInputStream(pathDecryptFile), new FileOutputStream(pathToEncrypt), key);
    }

    public void encryptImg(InputStream in, OutputStream out) throws Exception {
        aes.encrypt(in, out, key);
    }

    public void decryptImg(InputStream in, OutputStream out) throws Exception {
        try {
            aes.decrypt(in, out, key);
        } catch (Exception ex) {
            throw new Exception("Tal vez la contraseña es incorrecta");
        }
    }

    public Cipher getCiphertoEnc(OutputStream out) throws Exception {
        return (aes.getCiphertoEncZip(out, key));
    }

    public Cipher getCiphertoDec(InputStream in) throws Exception {
        return (aes.getCiphertoDecZip(in, key));
    }

}
