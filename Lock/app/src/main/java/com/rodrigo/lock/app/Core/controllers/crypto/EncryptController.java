package com.rodrigo.lock.app.Core.controllers.crypto;

import android.content.res.AssetManager;

import com.rodrigo.lock.app.Core.Clases.Accion;
import com.rodrigo.lock.app.Core.Clases.Archivo;
import com.rodrigo.lock.app.Core.Clases.FileType;
import com.rodrigo.lock.app.Core.controllers.FileController;
import com.rodrigo.lock.app.services.ExtractService;
import com.rodrigo.lock.app.Core.Utils.MediaUtils;
import com.rodrigo.lock.app.Core.crypto.AES.Crypto;
import com.rodrigo.lock.app.Core.Utils.Utils;
import com.rodrigo.lock.app.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.crypto.CipherOutputStream;

/**
 * Created by Rodrigo on 25/05/14.
 */

/*
     El inicio es distinto dependisndo del tipo de archivo despues
    -1 byte version 00000000
    -1 byte cabezales activos
        00000(vistasegura)(solo aca)(caducidad)
    -en caso de cabezal caducidad: 4byte para la fecha que es un int en formato aaaammdd

*/

public class EncryptController extends CryptoController {

    LinkedList<Archivo> inFileList;
    LinkedList<Long> inIDImgFileList;
    String toEncrypt;

    public EncryptController(FileController fc) {
        this.toEncrypt = fc.getOutFS();
        this.pass = fc.getPassword();
        this.cabezal = fc.getCabezal();
        this.inFileList = fc.getInFiles();
        this.accion =fc.getAccion();
    }

    OutputStream out = null;
    ZipOutputStream output = null;
    Crypto algo = null;
    int idN;

    @Override
    public void realizarTrabajo(ExtractService SM, int idN)  throws Exception{
        this.idN =idN;
        this.SM = SM;
        this.ctx = SM;
        inIDImgFileList = new LinkedList<Long>();
        encrypt();
    }



    private void encrypt() throws Exception {

        for (Archivo f : this.inFileList) {
            if (f.getTipo() == FileType.Imagen){
                inIDImgFileList.add(MediaUtils.isImageInGallery(f.getFile(), this.SM));
            }
            else if (f.getTipo() == FileType.Video){
                inIDImgFileList.add(MediaUtils.isVideoInGallery(f.getFile(), this.SM));
            }else{
                inIDImgFileList.add((long)-1);
            }
        }

        // Log.d("ecrtyptar", "entra a ecrtyptar");
        if (cabezal != null && cabezal.isSoloAca()) {
            pass = cabezal.mergeIdInPassword(pass, SM);
        }

        if (Accion.EncryptarConImagen == accion) {
            abrirArchivoImagen();
        } else {
            abrirArchivoSimple();
        }

        grabarCabezales();

        algo = new Crypto();
        algo.init(pass);

        grabarListaArchivos();

        finalizarArchivos();

        if (Accion.EncryptarConImagen ==accion) {
            MediaUtils.addImageGallery(new File(toEncrypt), SM);
        }



    }


    public void grabarCabezales() throws Exception {
        try {
         /*
            -1 byte version 00000000
            -1 byte cabezales activos
                000000(solo aca)(caducidad)
            -en cado de cabezal caducidad: 4byte para la fecha que es un int en formato aaaammdd

            */
            int tamcabezalCompleto = 2;

            byte[] version = new byte[1];
            Arrays.fill(version, Byte.parseByte("00000000", 2));

            byte[] cavezalesActivos = new byte[1];
            String activos = "00000";

            //vista segura
            activos = ((accion == Accion.EncryptarConImagen) ?   activos + "1" : activos + "0");
            //solo aca
            activos = ((cabezal != null && cabezal.isSoloAca()) ?   activos + "1" : activos + "0");
            //caducidad
            if (cabezal != null && cabezal.isCaducidad()) {
                activos = activos + "1";
                tamcabezalCompleto = tamcabezalCompleto + 4;
            } else {
                activos = activos + "0";
            }
            Arrays.fill(cavezalesActivos, Byte.parseByte(activos, 2));


            //se graba el resto cabezal
            byte[] cabezalCompleto = new byte[tamcabezalCompleto];
            cabezalCompleto[0] = version[0];
            cabezalCompleto[1] = cavezalesActivos[0];

            if (cabezal != null && cabezal.isCaducidad()) {
                byte[] caducidad = Utils.intToByteArray(cabezal.getFechaCaducidad());
                for (int i = 0; i < caducidad.length; i++) {
                    cabezalCompleto[i + 2] = caducidad[i];
                }
            }


            out.write(cabezalCompleto, 0, cabezalCompleto.length);

        } catch (Exception ex) {
            try {
                //no se a a;adido ala galeria basta elimnar asi
                Utils.delete(new File(toEncrypt));
            } catch (Exception e) {
            }

            throw new Exception(String.format(this.SM.getResources().getString(R.string.error_open), toEncrypt, ex.getMessage()));
        }
    }


    private void grabarListaArchivos() throws Exception {
        try {


            //se agrega la imagen encryptada al final
            /** se encryptan las imagenes**/
            out = new CipherOutputStream(out, algo.getCiphertoEnc(out));
            output = new ZipOutputStream(out);
            output.setLevel(Deflater.NO_COMPRESSION);


            double avanza = 0;
            byte[] buffer = new byte[1024];
            for (Archivo file : this.inFileList) {
                //System.out.println("File Added : " + file);
                ZipEntry ze = new ZipEntry(file.getFile().getName());
                output.putNextEntry(ze);

                FileInputStream inf = new FileInputStream(file.getFile());

                int len;
                while ((len = inf.read(buffer)) > 0) {
                    output.write(buffer, 0, len);
                    avanza = avanza + len;
                }
                inf.close();
                //feedback.setProgress((int) ((avanza * 100) / tamF) % 99);
            }
            output.closeEntry();
            output.close();


        } catch (Exception ex) {
            try {
                if (output != null) {
                    output.closeEntry();
                    output.close();
                    out.close();
                }
                //no se a a;adido ala galeria basta elimnar asi
                Utils.delete(new File(toEncrypt));
            } catch (Exception e) {
            }
            throw new Exception(String.format(this.SM.getResources().getString(R.string.error_lock2), ex.getMessage()));
        }
    }


    private void abrirArchivoImagen() throws Exception {
        try {
            //formato
            //-0xFF, 0xD8, start of image
            // -image to show
            // -0xFF, 0xD9, End Of Image
            // -pandorabox = 0x50, 0x41, 0x4E, 0x44, 0x4F, 0x52, 0x41, 0x42, 0x4F, 0x58
            //
            //despues sigue la version con los cabezales

            //se copia la imagen al nuevo archivo
            AssetManager assetManager = SM.getAssets();
            InputStream in = assetManager.open("imagenbloqueada.jpg");
            out = new FileOutputStream(toEncrypt);

            //secopia el splash
            //imagenbloqueada empieza con 0xff, 0xd8
            //y termina con 0xFF, 0xD9 por lo que no hay que chequear nada...
            byte[] buf = new byte[1024];
            int numRead = 0;
            while ((numRead = in.read(buf)) >= 0) {
                out.write(buf, 0, numRead);
            }

            //se graba el nombre del programa para identifical la imagen
            byte[] PANDORABOX = Utils.getPANDORABOX();
            out.write(PANDORABOX, 0, PANDORABOX.length);


        } catch (Exception ex) {
            try {
                Utils.delete(new File(toEncrypt));
            } catch (Exception e) {
            }

            throw new Exception(String.format(this.SM.getResources().getString(R.string.error_open), toEncrypt, ex.getMessage()));
        }
    }


    private void abrirArchivoSimple() throws Exception {
        try {
            out = new FileOutputStream(toEncrypt);

        } catch (Exception ex) {
            try {
                Utils.delete(new File(toEncrypt));
            } catch (Exception e) {
            }

            throw new Exception(String.format(this.SM.getResources().getString(R.string.error_open), toEncrypt, ex.getMessage()));
        }
    }


    public void finalizarArchivos() throws Exception {
        boolean error = false;
        String mensaje = null;
        String files = null;

        Iterator iterid = this.inIDImgFileList.iterator();
        for (Archivo f : this.inFileList) {
            long idImage = (Long) iterid.next();

            try {
                if (idImage ==-1) {
                    Utils.delete(f.getFile());
                }else{
                    if (f.getTipo() == FileType.Imagen ){
                        MediaUtils.deleteImageGallery(idImage, SM);
                    }else if(f.getTipo() == FileType.Video ){
                        MediaUtils.deleteVideoGallery(idImage, SM);
                    }

                    if (f.getFile().exists()){
                        Utils.delete(f.getFile());
                    }

                }

            } catch (Exception ex) {
                error = true;
                mensaje = ex.getMessage();
                files = files +", "+f.getFile().getAbsolutePath();
            }

        }

        if (error) {
            throw new Exception(String.format(this.SM.getResources().getString(R.string.error_delete2),files,  mensaje));
        }


    }


}
