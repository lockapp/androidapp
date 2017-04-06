package com.rodrigo.lock.app.Core.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Rodrigo on 10/08/2014.
 */
public class ByteArrayOfFile {
    InputStream is;
    byte[] bFile = new byte[1024];
    int inicio =0;
    int tope = 0;

    public ByteArrayOfFile(File f) throws FileNotFoundException {
        is = new FileInputStream(f);

    }

    public Byte getByte(int pos) throws IOException {
        while (tope <= pos){
            int numRead=is.read(bFile);
            inicio = tope;
            tope = tope + numRead;
        }
        return bFile[pos-inicio];
    }


    public void close(){
        try {
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
