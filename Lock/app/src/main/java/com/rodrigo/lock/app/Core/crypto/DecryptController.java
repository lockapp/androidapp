package com.rodrigo.lock.app.Core.crypto;

import android.content.Context;

import com.rodrigo.lock.app.Core.Clases.Accion;
import com.rodrigo.lock.app.Core.Clases.SingleMediaScanner;
import com.rodrigo.lock.app.Core.crypto.AES.Crypto;
import com.rodrigo.lock.app.Core.Controladores.CabezalController;
import com.rodrigo.lock.app.Core.Controladores.FileController;
import com.rodrigo.lock.app.Core.Controladores.ServiceManejador;
import com.rodrigo.lock.app.Core.Utils.ImgUtils;
import com.rodrigo.lock.app.Core.Utils.Utils;
import com.rodrigo.lock.app.R;

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

    LinkedList<File> outFileList;
    File inFile;
    Context c;

    String destFileDir;
    long idImage;
    boolean vistaSegura;

  //  private int progress = 0;
  //  private int size =0;


    //para see image
    public DecryptController(FileController fc, Context c) throws Exception{
        this.fc = fc;
        this.inFile = fc.getInFiles().getFirst();
        this.c = c;
        this.pass = fc.getPassword();
        //cabezal = new CabezalController();

        //checkAndInit();
    }

    InputStream in;
    ZipEntry ze;
    ZipInputStream input = null;

    public void checkAndInit() throws Exception {
        if (Accion.DesencryptarConImagen == fc.getAccion()){
            initImage();
        }else {
            initSimple();
        }
        openCabezales(vistaSegura);

        Crypto algo = new Crypto();
        algo.init(pass);

        in = new CipherInputStream(in, algo.getCiphertoDec(in));
        input = new ZipInputStream(in);

        ze = input.getNextEntry();
        //chequea si desbloqueo
        if (ze == null) {
            throw new Exception(this.c.getResources().getString(R.string.error_password));
        }
    }



    int idN;
    @Override
    public void realizarTrabajo (ServiceManejador SM, int idN)  throws Exception{
        this.SM = SM;
        this.c = SM;
        outFileList = new LinkedList<File>();
        this.idN = idN;

        decrypt();
    }




    private void decrypt()  throws Exception{

            //this.SM.setIndeterminateProgressBar(idN);
            //se chequea si es imagen
            this.idImage = ImgUtils.isImageInGallery(this.inFile, c);

            //se sacan los archivos
            extraerTodosLosArchivos();

            //se elimina el original
            try {
                if (this.idImage > 0) {
                    ImgUtils.deleteImageGallery(idImage, SM);
                } else {
                    Utils.delete(this.inFile);
                }
            } catch (Exception ex) {
                // throw  new Exception("No se a puede eliminar el archivo a bloquear, probablemente este abierto. RAZON" + ex.getMessage());
            }

            //si son imagenes se agregan a la galeria
            for (File f :   this.outFileList) {
                if (Utils.isExtensionImage(Utils.getExtensionFile(f.getName()))) {
                    ImgUtils.addImageGallery(f.getAbsolutePath(), SM);
                }else if (Utils.isExtensionVideo(Utils.getExtensionFile(f.getName()))){
                    new SingleMediaScanner(SM, f);
                }
            }



    }



    public void initImage() throws Exception {
        try {
            in = new FileInputStream(inFile);
            in.skip(fc.getOffset());
        } catch (Exception e) {
            throw new Exception(String.format(c.getResources().getString(R.string.error_open), inFile,  e.getMessage()));
        }
    }


    public void initSimple() throws Exception {
        try {
            in = new FileInputStream(inFile);
        } catch (Exception e) {
            throw new Exception(String.format(c.getResources().getString(R.string.error_open), inFile,  e.getMessage()));
        }
    }


    public void openCabezales(boolean usarVistaSegura) throws Exception {
        //se chequea la version

        byte[] version = new byte[1];
        in.read(version);
        if ((version[0] != ((byte) 0x00))) {
            throw new Exception(c.getResources().getString(R.string.error_version));
        }

        byte[] cavezalesActivos = new byte[1];
        in.read(cavezalesActivos);

        //vista segura
        if ((cavezalesActivos[0] & Byte.parseByte("00000100", 2)) == Byte.parseByte("00000000", 2) ){
            if (usarVistaSegura){
                throw new Exception(this.c.getResources().getString(R.string.error_nosecureview));
            }
        }

        //solo aca
        if ((cavezalesActivos[0] & Byte.parseByte("00000010", 2)) == Byte.parseByte("00000010", 2) ){
            //cabezal.setSoloAca(true);
            pass = CabezalController.mergeIdInPassword(pass, c);
        }

        //caducidad
        if ((cavezalesActivos[0] & Byte.parseByte("00000001", 2)) == Byte.parseByte("00000001", 2) ){
            //cabezal.setCaducidad(true);
            byte[] caducidad = new byte[4];
            in.read(caducidad);
            int fechaCaducidad = Utils.byteArrayToInt(caducidad);

            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
            String actualDate = df.format(c.getTime());

            if (Integer.valueOf(actualDate) > fechaCaducidad) {
                throw new Exception(this.c.getResources().getString(R.string.error_defeated));
            }
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
                    newFile =new File (Utils.getPathFileNoExists(inFile.getParent() + File.separator, Utils.removeExtensionFile(fileName) ,Utils.getExtensionFile(fileName)));

                }

                this.outFileList.add(newFile);
                new File(newFile.getParent()).mkdirs();
                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = input.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                    //progress +=len;
                }
                fos.close();
                ze = input.getNextEntry();
               // SM.updateProgressBar(size, progress);

            }
            input.closeEntry();
            input.close();
            in.close();


        } catch (Exception e) {
            //boolean error = false;
            // String mensaje;
            for (File f : this.outFileList) {
                try {
                    Utils.delete(f);
                } catch (Exception ex) {
                    // error = true;
                    // mensaje = ex.getMessage();
                }
            }
            throw new Exception(String.format(c.getResources().getString(R.string.error_unlock), inFile,  e.getMessage()));
        }

    }


}
