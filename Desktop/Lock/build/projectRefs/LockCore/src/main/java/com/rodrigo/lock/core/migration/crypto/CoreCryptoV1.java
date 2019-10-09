package com.rodrigo.lock.core.migration.crypto;


import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Rodrigo on 12/12/2014.
 */
public class CoreCryptoV1 {

    /*
     * AES
     */

    private static final int KEY_SIZE_BITS = 256;
    private static final int KEY_SIZE_BYTES = KEY_SIZE_BITS / 8;
    private static final int SALT_SIZE_BYTE = KEY_SIZE_BYTES;


    public class AES implements  CoreCrypto{

        private static final String SECRET_KEY_ALGORITHM = "PBKDF2WithHmacSHA1";

        private static final String ALGORITHM = "AES";
        private static final String MODE = "CBC";
        private static final String PADDING = "PKCS5Padding";
        private static final String CIPHER_TRANSFORMATION = ALGORITHM + "/" + MODE + "/" + PADDING;


        private byte[] ivBytes;
        private SecretKey   key;
        private Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
        private int IV_LENGTH_BYTE;

        public AES()
                throws NoSuchAlgorithmException, NoSuchPaddingException, NoSuchProviderException,
                InvalidParameterSpecException, InvalidKeyException, InvalidAlgorithmParameterException {
            IV_LENGTH_BYTE = cipher.getBlockSize();
        }


        @Override
        public Cipher getCiphertoEncZip(OutputStream out,String  password) throws Exception {
            byte[] salt = Utils.getRandomBytes(SALT_SIZE_BYTE);
            out.write(salt, 0, SALT_SIZE_BYTE);

            //se crea y se guarda el iv
            ivBytes = Utils.getRandomBytes(IV_LENGTH_BYTE);
            out.write(ivBytes, 0, IV_LENGTH_BYTE);

            key = CoreCryptoV1.PBKDF2.pbkdf2(password, salt, 1000);

            cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(ivBytes));

            return cipher;
        }

        @Override
        public Cipher getCiphertoDecZip(InputStream in, String password) throws Exception {
            byte[] salt = new byte[SALT_SIZE_BYTE];
            if (in.read(salt) < SALT_SIZE_BYTE) {
                throw new IllegalArgumentException("Invalid file length (needs a full block for salt)");
            };

            key = CoreCryptoV1.PBKDF2.pbkdf2(password, salt, 1000);

            //se carga el iv
            ivBytes = new byte[IV_LENGTH_BYTE];
            if (in.read(ivBytes) < IV_LENGTH_BYTE) {
                throw new IllegalArgumentException("Invalid file length (needs a full block for iv)");
            };

            cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(ivBytes));
            return cipher;
        }

        @Override
        public String encrypt(String plaintext)
                throws NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException,
                InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
            if (plaintext.length() == 0) {
                return null;
            }
            Charset PLAIN_TEXT_ENCODING = Charset.forName("UTF-8");       // character encoding

            cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(ivBytes));
            byte[] encrypted = cipher.doFinal(plaintext.getBytes(PLAIN_TEXT_ENCODING));
            return Utils.byteArrayToBase64String(encrypted);
        }








        // Buffer used to transport the bytes from one stream to another
     /*   byte[] buf = new byte[1024];

        public void encrypt(InputStream in, OutputStream out, String passphrase) throws Exception {
            //se crea y se guarda el iv
            ivBytes = generateIV();
            out.write(ivBytes, 0, KEY_SIZE_BYTES);

            setStringToKey(passphrase);

            cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(ivBytes));

            // Bytes written to out will be encrypted
            out = new CipherOutputStream(out, cipher);

            // Read in the cleartext bytes and write to out to encrypt
            int numRead = 0;
            while ((numRead = in.read(buf)) >= 0) {
                out.write(buf, 0, numRead);
            }
            out.close();
            in.close();

        }*/






    }






    /*
     * PBKDF2: deriveKey
     */
    private static class PBKDF2 {

        public static SecretKey  pbkdf2(String password, byte[] salt,  int pbkdf2Iterations)
                throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException {

            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(AES.SECRET_KEY_ALGORITHM);
            PBEKeySpec pbeKeySpec = new PBEKeySpec(password.toCharArray(), salt, pbkdf2Iterations, CoreCryptoV1.KEY_SIZE_BITS);
            byte[] keyBytes = keyFactory.generateSecret(pbeKeySpec).getEncoded();

            return new SecretKeySpec(keyBytes, AES.ALGORITHM);

        }



    }


    /*
     * Utils
     */
    public static class Utils {

        public static byte[] getRandomBytes(int len) {
            Random ranGen = new SecureRandom();
            byte[] aesKey = new byte[len];
            ranGen.nextBytes(aesKey);
            return aesKey;
        }



        public static String byteArrayToBase64String(byte[] raw) {
            return new String(Base64Coder.encode(raw));
        }


    }

    /*
     * Base64Coder
     */
    private static class Base64Coder {
        // Mapping table from 6-bit nibbles to Base64 characters.
        private static final char[] map1 = new char[64];

        static {
            int i = 0;
            for (char c = 'A'; c <= 'Z'; c++) {
                map1[i++] = c;
            }
            for (char c = 'a'; c <= 'z'; c++) {
                map1[i++] = c;
            }
            for (char c = '0'; c <= '9'; c++) {
                map1[i++] = c;
            }
            map1[i++] = '+';
            map1[i++] = '/';
        }
        // Mapping table from Base64 characters to 6-bit nibbles.
        private static final byte[] map2 = new byte[128];

        static {
            for (int i = 0; i < map2.length; i++) {
                map2[i] = -1;
            }
            for (int i = 0; i < 64; i++) {
                map2[map1[i]] = (byte) i;
            }
        }

        /**
         * Encodes a byte array into Base64 format. No blanks or line breaks are
         * inserted in the output.
         *
         * @param in An array containing the data bytes to be encoded.
         * @return A character array containing the Base64 encoded data.
         */
        public static char[] encode(byte[] in) {
            return encode(in, 0, in.length);
        }


        /**
         * Encodes a byte array into Base64 format. No blanks or line breaks are
         * inserted in the output.
         *
         * @param in An array containing the data bytes to be encoded.
         * @param iOff Offset of the first byte in
         * <code>in</code> to be processed.
         * @param iLen Number of bytes to process in
         * <code>in</code>, starting at
         * <code>iOff</code>.
         * @return A character array containing the Base64 encoded data.
         */
        public static char[] encode(byte[] in, int iOff, int iLen) {
            int oDataLen = (iLen * 4 + 2) / 3;       // output length without padding
            int oLen = ((iLen + 2) / 3) * 4;         // output length including padding
            char[] out = new char[oLen];
            int ip = iOff;
            int iEnd = iOff + iLen;
            int op = 0;
            while (ip < iEnd) {
                int i0 = in[ip++] & 0xff;
                int i1 = ip < iEnd ? in[ip++] & 0xff : 0;
                int i2 = ip < iEnd ? in[ip++] & 0xff : 0;
                int o0 = i0 >>> 2;
                int o1 = ((i0 & 3) << 4) | (i1 >>> 4);
                int o2 = ((i1 & 0xf) << 2) | (i2 >>> 6);
                int o3 = i2 & 0x3F;
                out[op++] = map1[o0];
                out[op++] = map1[o1];
                out[op] = op < oDataLen ? map1[o2] : '=';
                op++;
                out[op] = op < oDataLen ? map1[o3] : '=';
                op++;
            }
            return out;
        }


        // Dummy constructor.
        private Base64Coder() {
        }
    }
}
