package com.rodrigo.lock.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import com.rodrigo.lock.core.Utils.FileUtils;
import com.rodrigo.lock.core.enums.CryptoAction;
import com.rodrigo.lock.core.enums.FileType;
import com.rodrigo.lock.core.v1.DecryptHandler;
import com.rodrigo.lock.core.v2.DecryptHandlerV2;
import com.rodrigo.lock.core.v2.EncryptHandlerV2;

import exceptions.LockException;

public class CryptoHandlerUtils {

	public static CryptoHandler resolverAccion(List<File> archivosIn) throws Exception {
		if (archivosIn == null || archivosIn.size() == 0) {
			throw new LockException(LockException.not_found);
		} else if (archivosIn.size() == 1) {
			return  getActionForFile(archivosIn.get(0));
		} else {
			return new EncryptHandlerV2(archivosIn);
		}
	}
	

	private static CryptoHandler getActionForFile(File inF) throws Exception {		
		// es cualquier archivo
		FileType fileType = FileUtils.getFileType(inF);
		if (fileType == FileType.OpenPBX_V2) {
			return new DecryptHandlerV2(inF);
			// es imagen
		}else if (fileType == FileType.OpenPBX) {
			return new DecryptHandler(0,  Arrays.asList(inF), CryptoAction.TO_DECRYPT);
			
		} else if (fileType == FileType.Imagen) {
			long offset = isImageEncrypted(inF);			
			// esta contenido en una imagen
			if (offset > 0) {
				return new DecryptHandler(offset,  Arrays.asList(inF), CryptoAction.TO_DECRYPT_WITH_IMAGE);
			}
		}
		return new EncryptHandlerV2(Arrays.asList(inF));
	}
	
	

    private static int isImageEncrypted(File f) throws Exception {
        //byte[] PANDORABOX = FileUtils.PANDORABOX;
    	File slpash = new File("file:resources/images/imagenbloqueada.jpg");
        long size = slpash.length();

        if (size + FileUtils.PANDORABOX.length > f.length())
            return -1;

        InputStream in = new FileInputStream(f);
        in.skip(size);

        byte[] bFile = new byte[FileUtils.PANDORABOX.length];
        int leido = 0;
        while (leido<FileUtils.PANDORABOX.length){
            leido+=in.read(bFile, leido, FileUtils.PANDORABOX.length-leido);
        }
        in.close();

        int iter = 0;
        boolean ret = true;

        while ( iter < FileUtils.PANDORABOX.length ) {
            if  (FileUtils.PANDORABOX[iter] != bFile[ iter])
                return  -1;
            iter++;
        }

        return  (iter + (int)size);
    }
    
    
  //metodo para ver si es una imagen encryptada que sirve con cualquier imagen en el inicio
    /*   private int isImageEncrypted(File f) throws Exception {
           InputStream in = new FileInputStream(f);
           byte[] bFile = new byte[(int) f.length()];

           in.read(bFile);
           in.close();


           int base = 0;
           while (base < bFile.length) {

               if (((byte) 0xFF) == bFile[base]) {

                   if (((byte) 0xD9) == bFile[base + 1]) {
                       base = base + 2;
                       byte[] PANDORABOX = Utils.getPANDORABOX();

                       int iter = 0;
                       boolean ret = true;

                       while (ret && (iter < PANDORABOX.length) && ((base + iter) < bFile.length)) {
                           ret = (PANDORABOX[iter] == bFile[base + iter]);
                           iter++;
                       }

                       if (ret && (iter == PANDORABOX.length))
                           return base + iter;
                       else
                           return -1;


                   } else if (!((((byte) 0x00) == bFile[base + 1]) || (((byte) 0x01) == bFile[base + 1]) ||
                           ((((byte) 0xD0) <= bFile[base + 1]) && (((byte) 0xD8) >= bFile[base + 1])))) {
                       byte[] temp = new byte[2];
                       int result = ((bFile[base + 3] << 8) & 0x0000ff00) | (bFile[base + 2] & 0x000000ff);
                       base = base + result + 3;
                   }
               }
               base++;
           }
           return -1;
       }
   */
	
	
	
	

}
