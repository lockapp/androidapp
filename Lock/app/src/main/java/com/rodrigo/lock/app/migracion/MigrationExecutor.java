package com.rodrigo.lock.app.migracion;

import android.text.TextUtils;
import android.util.Log;

import com.rodrigo.lock.app.old.Core.crypto.AES.Crypto;
import com.rodrigo.lock.core.EncryptedFileSystem;
import com.rodrigo.lock.core.utils.FileUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.crypto.CipherInputStream;

/**
 * Created by Rodrigo on 03/12/2016.
 */

public class MigrationExecutor {
    File inFile;
    long offset;
    //DataError lastError;

    InputStream in;
    ZipEntry ze;
    ZipInputStream zipInput = null;

    boolean checkAndInit = false;
    String pass;

    EncryptedFileSystem out;

    public void setOut(EncryptedFileSystem out) {
        this.out = out;
    }

    public EncryptedFileSystem getOut() {
        return this.out;
    }

    public MigrationExecutor (String pass, File inFile, long offset ){
        this.pass =pass;
        this.inFile =inFile;
        this.offset =offset;
    }


    public void checkAndInit() throws Exception {
        if (!checkAndInit) {
            if (offset > 0) {
                initImage();
            } else {
                initSimple();
            }

            openCabezales(false);

            zipInput = new ZipInputStream(in);
            ze = zipInput.getNextEntry();
            //chequea si desbloqueo
            if (ze == null) {
                //lastError = new DataError(DataError.ERROR.ERROR_PASSWORD, this.ctx.getResources().getString(R.string.error_password));
                throw new Exception(/*lastError.getDescripcion()*/);
            }

            checkAndInit = true;
        }

    }


    private void decrypt()  throws Exception {

        //this.SM.setIndeterminateProgressBar(idN);
        //se chequea si es imagen
        //se sacan los archivos
        //extraerTodosLosArchivos();
        //eliminarOriginal();


    }


    private void eliminarOriginal() {
        try {
//            this.idImage = MediaUtils.isImageInGallery(this.inFile, ctx);
//
//            if (this.idImage > 0) {
//                MediaUtils.deleteImageGallery(idImage, ctx);
//            } else {
//                FileUtils.delete(this.inFile);
//            }
        } catch (Exception ex) {
            Log.d("en desencriptar", "error al borrar origina");
            // throw  new Exception("No se a puede eliminar el archivo a bloquear, probablemente este abierto. RAZON" + ex.getMessage());
        }
    }


    public void initImage() throws Exception {
        //try {
            in = new FileInputStream(inFile);
            in.skip(offset);
       /* } catch (Exception e) {
            //lastError = new DataError(DataError.ERROR.ERROR_OTRO, String.format(ctx.getResources().getString(R.string.error_open), inFile, e.getMessage()));
            throw new Exception(lastError.getDescripcion());
        }*/
    }


    public void initSimple() throws Exception {
       // try {
            in = new FileInputStream(inFile);
//        } catch (Exception e) {
//            lastError = new DataError(DataError.ERROR.ERROR_OTRO, (String.format(ctx.getResources().getString(R.string.error_open), inFile, e.getMessage())));
//            throw new Exception(lastError.getDescripcion());
//        }
    }


    private void openCabezales(boolean usarVistaSegura) throws Exception {

        //se chequea la version
        byte[] version = new byte[1];
        in.read(version);
        if ((version[0] > ((byte) 0x01))) {
            //lastError = new DataError(DataError.ERROR.ERROR_VERSION, ctx.getResources().getString(R.string.error_version));
            throw new Exception(/*lastError.getDescripcion()*/);
        }

        //se empiezan a chequear los cabezales
        byte[] cavezalesActivos = new byte[1];
        in.read(cavezalesActivos);

        //vista segura
        if ((cavezalesActivos[0] & Byte.parseByte("00000100", 2)) == Byte.parseByte("00000000", 2)) {
           /* if (usarVistaSegura) {
                //lastError = new DataError(DataError.ERROR.ERROR_OTRO, ctx.getResources().getString(R.string.error_nosecureview));
                throw new Exception(lastError.getDescripcion());
            }*/
        }


        //caducidad
        if ((cavezalesActivos[0] & Byte.parseByte("00000001", 2)) == Byte.parseByte("00000001", 2)) {
            //cabezal.setCaducidad(true);
            byte[] caducidad = new byte[4];
            in.read(caducidad);
            int fechaCaducidad = FileUtils.byteArrayToInt(caducidad);

            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
            String actualDate = df.format(c.getTime());

            if (Integer.valueOf(actualDate) > fechaCaducidad) {
                eliminarOriginal();
                //lastError = new DataError(DataError.ERROR.ERROR_VENCIMIENTO, ctx.getResources().getString(R.string.error_defeated));
                throw new Exception(/*lastError.getDescripcion()*/);
                //eliminar archivo
            }
        }


        //cifrar
        if (((cavezalesActivos[0] & Byte.parseByte("00010000", 2)) == Byte.parseByte("00010000", 2)) || (version[0] == ((byte) 0x00))) {
            if (TextUtils.isEmpty(pass)) {
                //lastError = new DataError(DataError.ERROR.ERROR_PASSWORD, ctx.getResources().getString(R.string.empty_password));
                throw new Exception(/*lastError.getDescripcion()*/);
            }


            //solo aca
            if ((cavezalesActivos[0] & Byte.parseByte("00000010", 2)) == Byte.parseByte("00000010", 2)) {
                //cabezal.setSoloAca(true);
                pass = MigrarUtilsDeprecated.mergeIdInPassword(pass);
            }

            Crypto algo = new Crypto();
            if ((version[0] == ((byte) 0x00))) {
                algo.initV0();
            } else {
                algo.initV1();
            }
            in = new CipherInputStream(in, algo.getCiphertoDec(in, pass));

        }


        //prhoibir extraer
        if ((cavezalesActivos[0] & Byte.parseByte("00001000", 2)) == Byte.parseByte("00001000", 2)) {
           // this.esExtraible = false;
        }

    }


    public void extraerTodosLosArchivos(File baseFolder) throws Exception {
        try {

            byte[] buffer = new byte[1024];


            while (ze != null) {
                String fileName = ze.getName();
                File newFile = new File(baseFolder.getPath() + File.separator + fileName);

                if (newFile.exists()) {
                    //throw new Exception("Ya existe un archivo con el mismo nombre a desbloquear " + newFile.getAbsolutePath());
                    newFile = new File(FileUtils.createNewFileNameInPath(baseFolder.getPath()+ File.separator, FileUtils.removeExtensionFile(fileName), FileUtils.getExtensionFile(fileName)));

                }

                //this.outFileList.add(new Archivo(newFile));
                new File(newFile.getParent()).mkdirs();
                FileOutputStream fos = new FileOutputStream(newFile);
                BufferedOutputStream out = new BufferedOutputStream(fos);

                BufferedInputStream in = new BufferedInputStream(zipInput);
                int len;
                while ((len = in.read(buffer, 0, 1024)) >= 0) {
                    out.write(buffer, 0, len);
                    //progress +=len;
                }
                out.close();
                fos.close();
                ze = zipInput.getNextEntry();
                // SM.updateProgressBar(size, progress);

            }
            zipInput.closeEntry();
            zipInput.close();
            in.close();


        } catch (Exception e) {
            //boolean error = false;
            // String mensaje;
//            for (Archivo f : this.outFileList) {
//                try {
//                    FileUtils.delete(f.getFile());
//                } catch (Exception ex) {
//                    // error = true;
//                    // mensaje = ex.getMessage();
//                }
//            }
//            throw new Exception(String.format(ctx.getResources().getString(R.string.error_unlock), inFile, e.getMessage()));
        }

    }

}
