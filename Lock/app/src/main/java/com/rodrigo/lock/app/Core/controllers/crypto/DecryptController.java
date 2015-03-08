package com.rodrigo.lock.app.Core.controllers.crypto;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.rodrigo.lock.app.Core.Clases.Accion;
import com.rodrigo.lock.app.Core.Clases.Archivo;
import com.rodrigo.lock.app.Core.Clases.DataError;
import com.rodrigo.lock.app.Core.Clases.FileHeader;
import com.rodrigo.lock.app.Core.Clases.FileType;
import com.rodrigo.lock.app.Core.Utils.FileUtils;
import com.rodrigo.lock.app.Core.Utils.MediaUtils;
import com.rodrigo.lock.app.Core.Utils.Utils;
import com.rodrigo.lock.app.Core.crypto.AES.Crypto;
import com.rodrigo.lock.app.R;
import com.rodrigo.lock.app.services.ExtractService;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.crypto.CipherInputStream;

/**
 * Created by Rodrigo on 25/05/14.
 */
public class DecryptController extends CryptoController {

    LinkedList<Archivo> outFileList;
    File inFile;
   // String destFileDir;
    private long idImage;
    boolean vistaSegura;
    long offset;
    boolean esExtraible =true;
    DataError lastError;

    public boolean esExtraible(){
        return esExtraible;
    }



    //para see image
    public DecryptController(Context ctx, File inFile, String pass, Accion accio, long offset, String name) {
        this.ctx = ctx;
        this.inFile = inFile;
        this.pass = pass;
        this.accion = accio;
        this.offset=offset;
        lastError= null;
        this.name =name;
        //cabezal = new CabezalController();
        //checkAndInit();
    }

    InputStream in;
    ZipEntry ze;
    ZipInputStream zipInput = null;

    boolean checkAndInit = false;
    @Override
    public void checkAndInit() throws Exception {
        if (!checkAndInit){
            if (Accion.DesencryptarConImagen == accion){
                initImage();
            }else {
                initSimple();
            }

            openCabezales(vistaSegura);

            zipInput = new ZipInputStream(in);
            ze = zipInput.getNextEntry();
            //chequea si desbloqueo
            if (ze == null) {
                lastError = new DataError(DataError.ERROR.ERROR_PASSWORD, this.ctx.getResources().getString(R.string.error_password));
                throw new Exception(lastError.getDescripcion());
            }

            checkAndInit = true;
        }

    }



    @Override
    public void realizarTrabajo (ExtractService SM)  throws Exception{
        this.SM = SM;
        this.ctx = SM;
        outFileList = new LinkedList<Archivo>();

        decrypt();
    }




    private void decrypt()  throws Exception{

            //this.SM.setIndeterminateProgressBar(idN);
            //se chequea si es imagen


            //se sacan los archivos
            if (esExtraible){
                extraerTodosLosArchivos();
            }else{
                lastError = new DataError(DataError.ERROR.ERROR_EXTRACT, this.ctx.getResources().getString(R.string.error_noextract));
                throw new Exception(lastError.getDescripcion());
            }

            //se elimina el original
            eliminarOriginal();

            //si son imagenes se agregan a la galeria
            for (Archivo f :   this.outFileList) {
                try{
                    if (f.getTipo() == FileType.Imagen) {
                        MediaUtils.addImageGallery(f.getFile(), ctx);
                    }else if (f.getTipo() == FileType.Video){
                        MediaUtils.addVideoGallery(f.getFile(), ctx);
                    }
                }catch (Exception e){
                    ctx.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(f.getFile())));

                }

            }



    }


    private void eliminarOriginal(){
        try {
            this.idImage = MediaUtils.isImageInGallery(this.inFile, ctx);

            if (this.idImage > 0) {
                MediaUtils.deleteImageGallery(idImage, ctx);
            } else {
                FileUtils.delete(this.inFile);
            }
        } catch (Exception ex) {
            Log.d("en desencriptar", "error al borrar origina");
            // throw  new Exception("No se a puede eliminar el archivo a bloquear, probablemente este abierto. RAZON" + ex.getMessage());
        }
    }



    public void initImage() throws Exception {
        try {
            in = new FileInputStream(inFile);
            in.skip(offset);
        } catch (Exception e) {
            lastError = new DataError(DataError.ERROR.ERROR_OTRO, String.format(ctx.getResources().getString(R.string.error_open), inFile, e.getMessage()));
            throw new Exception(lastError.getDescripcion());
        }
    }


    public void initSimple() throws Exception {
        try {
            in = new FileInputStream(inFile);
        } catch (Exception e) {
            lastError = new DataError(DataError.ERROR.ERROR_OTRO, (String.format(ctx.getResources().getString(R.string.error_open), inFile, e.getMessage())));
            throw new Exception(lastError.getDescripcion());
        }
    }


    private void openCabezales(boolean usarVistaSegura) throws Exception {

        //se chequea la version
        byte[] version = new byte[1];
        in.read(version);
        if ((version[0] > ((byte) 0x01))) {
            lastError = new DataError(DataError.ERROR.ERROR_VERSION, ctx.getResources().getString(R.string.error_version));
            throw new Exception(lastError.getDescripcion());
        }

        //se empiezan a chequear los cabezales
        byte[] cavezalesActivos = new byte[1];
        in.read(cavezalesActivos);

        //vista segura
        if ((cavezalesActivos[0] & Byte.parseByte("00000100", 2)) == Byte.parseByte("00000000", 2) ){
            if (usarVistaSegura){
                lastError = new DataError(DataError.ERROR.ERROR_OTRO, ctx.getResources().getString(R.string.error_nosecureview));
                throw new Exception(lastError.getDescripcion());
            }
        }


            //caducidad
        if ((cavezalesActivos[0] & Byte.parseByte("00000001", 2)) == Byte.parseByte("00000001", 2) ){
            //cabezal.setCaducidad(true);
            byte[] caducidad = new byte[4];
            in.read(caducidad);
            int fechaCaducidad = FileUtils.byteArrayToInt(caducidad);

            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
            String actualDate = df.format(c.getTime());

            if (Integer.valueOf(actualDate) > fechaCaducidad) {
                eliminarOriginal();
                lastError = new DataError(DataError.ERROR.ERROR_VENCIMIENTO, ctx.getResources().getString(R.string.error_defeated));
                throw new Exception(lastError.getDescripcion());
                //eliminar archivo
            }
        }



        //cifrar
        if (((cavezalesActivos[0] & Byte.parseByte("00010000", 2)) == Byte.parseByte("00010000", 2) ) || (version[0] == ((byte) 0x00))){
            if (TextUtils.isEmpty(pass)){
                lastError = new DataError(DataError.ERROR.ERROR_PASSWORD, ctx.getResources().getString(R.string.empty_password));
                throw new Exception(lastError.getDescripcion());
            }


            //solo aca
            if ((cavezalesActivos[0] & Byte.parseByte("00000010", 2)) == Byte.parseByte("00000010", 2) ){
                //cabezal.setSoloAca(true);
                pass = FileHeader.mergeIdInPassword(pass, ctx);
            }

            Crypto algo = new Crypto();
            if ((version[0] == ((byte) 0x00))) {
                algo.initV0();
            }else{
                algo.initV1();
            }
            in = new CipherInputStream(in, algo.getCiphertoDec(in, pass));

        }



        //prhoibir extraer
        if ((cavezalesActivos[0] & Byte.parseByte("00001000", 2)) == Byte.parseByte("00001000", 2) ){
            this.esExtraible = false;
        }

    }


    public void extraerTodosLosArchivos() throws Exception {
        try {

            byte[] buffer = new byte[1024];

            while (ze != null) {
                String fileName = ze.getName();
                File newFile = new File(inFile.getParent() + File.separator + fileName);

                if (newFile.exists()) {
                    //throw new Exception("Ya existe un archivo con el mismo nombre a desbloquear " + newFile.getAbsolutePath());
                    newFile =new File (FileUtils.createNewFileNameInPath(inFile.getParent() + File.separator, FileUtils.removeExtensionFile(fileName) ,FileUtils.getExtensionFile(fileName)));

                }

                this.outFileList.add(new Archivo(newFile));
                new File(newFile.getParent()).mkdirs();
                FileOutputStream fos = new FileOutputStream(newFile);
                BufferedOutputStream out = new BufferedOutputStream(fos);

                BufferedInputStream in = new BufferedInputStream(zipInput);
                int len;
                while ((len = in.read(buffer,0,1024)) >= 0) {
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
            for (Archivo f : this.outFileList) {
                try {
                    FileUtils.delete(f.getFile());
                } catch (Exception ex) {
                    // error = true;
                    // mensaje = ex.getMessage();
                }
            }
            throw new Exception(String.format(ctx.getResources().getString(R.string.error_unlock), inFile, e.getMessage()));
        }

    }


    public File getInFile() {
        return inFile;
    }


    public DataError getLastError(){
        DataError r = lastError;
        lastError = null;
        return r;
    }
}
