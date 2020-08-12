package com.rodrigo.lock.core.v2;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.crypto.BadPaddingException;
import javax.crypto.CipherInputStream;

import com.rodrigo.lock.core.CryptoHandler;
import com.rodrigo.lock.core.Utils.FileUtils;
import com.rodrigo.lock.core.Utils.TextUtils;
import com.rodrigo.lock.core.archives.tar2.TarArchiveEntry;
import com.rodrigo.lock.core.archives.tar2.TarArchiveInputStream;
import com.rodrigo.lock.core.clases.DecryptListener;
import com.rodrigo.lock.core.crypto.AES.CoreCrypto;
import com.rodrigo.lock.core.crypto.AES.Crypto;
import com.rodrigo.lock.core.enums.CryptoAction;
import com.rodrigo.lock.core.v2.clases.DecryptOptions;

import exceptions.LockException;

public class DecryptHandlerV2 extends CryptoHandler {
	//private long offset;

	public DecryptHandlerV2(File archivosIn) {
		super();
		this.accion = CryptoAction.TO_DECRYPT;
		inFile = archivosIn;
	}
	
	public File getInFile(){
		return inFile;
	}
	

	DecryptOptions opciones;
	DecryptListener listener;
	File inFile;
	boolean inicializado = false;
	InputStream in;
	TarArchiveEntry ze;
	TarArchiveInputStream zipInput = null;

	boolean esExtraible = true;

	public void init(DecryptOptions opciones) throws Exception {
		this.opciones = opciones;

		if (!inFile.exists()){
			String[] lparam = {inFile.getName()};
       	 	throw new LockException(LockException.file_not_found_2,  lparam);
		}
		
		
		if (!inicializado) {

			in = new FileInputStream(inFile);

			CoreCrypto algo = Crypto.createCryptoV1();   				
			in = new CipherInputStream(in, algo.getCiphertoDecZip(in, opciones.getPassword()));

			zipInput = new TarArchiveInputStream(in);
			ze = zipInput.getNextTarEntry();
			// chequea si desbloqueo
			if (ze == null) {
				throw new LockException(LockException.error_password);
			}

			inicializado = true;
		}

	}



	public void decrypt(DecryptListener listener) throws Exception {
		this.listener = listener;
		
		extraerTodosLosArchivos();
		
		if (!this.opciones.isConservarOriginal()) {
			eliminarOriginal();
		}

		// si son imagenes se agregan a la galeria
		/*
		 * for (Archivo f : this.outFileList) { try{ if (f.getTipo() ==
		 * FileType.Imagen) { MediaUtils.addImageGallery(f.getFile(), ctx);
		 * }else if (f.getTipo() == FileType.Video){
		 * MediaUtils.addVideoGallery(f.getFile(), ctx); } }catch (Exception e){
		 * ctx.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
		 * Uri.fromFile(f.getFile())));
		 * 
		 * }
		 * 
		 * }
		 */

	}
	
	private void eliminarOriginal() {
		try {
			FileUtils.delete(this.inFile);
		} catch (Exception ex) {
			// Log.d("en desencriptar", "error al borrar origina");
			// throw new
			// Exception("No se a puede eliminar el archivo a bloquear, probablemente este abierto. RAZON"
			// + ex.getMessage());
		}
	}

	private void extraerTodosLosArchivos() throws Exception {
		List<File> outFileList = new LinkedList<>();
		try {

			byte[] buffer = new byte[1024];

			while (ze != null) {
				
					// this.outFileList.add(new Archivo(newFile));
				
/*				new File(newFile.getParent()).mkdirs();
				FileOutputStream fos = new FileOutputStream(newFile);
				BufferedOutputStream out = new BufferedOutputStream(fos);

				BufferedInputStream in = new BufferedInputStream(zipInput);
				int len;
				while ((len = in.read(buffer, 0, 1024)) >= 0) {
					out.write(buffer, 0, len);
					// progress +=len;
				}
				out.close();
				fos.close();
*/
				
				File newFile;
				if (ze.isDirectory()) {					
					newFile = new File(this.opciones.getRutaSalida() + File.separator +  ze.getName());							
					newFile.mkdirs();
					outFileList.add(newFile);
				}else {
					String fileName = ze.getName();
					newFile = new File(this.opciones.getRutaSalida() + File.separator + fileName);
					if (newFile.exists()) {
						newFile = new File(FileUtils.createNewFileNameInPath(this.opciones.getRutaSalida() + File.separator,FileUtils.removeExtensionFile(fileName),	FileUtils.getExtensionFile(fileName)));
					}					
					new File(newFile.getParent()).mkdirs();
					outFileList.add(newFile);					
					
					int count;
					FileOutputStream fos = new FileOutputStream(newFile);
					BufferedOutputStream dest = new BufferedOutputStream(fos);
					while ((count = zipInput.read(buffer, 0, 1024)) != -1) {
						dest.write(buffer, 0, count);
					}
					dest.close();
					
				}

				

				///////////////////
				this.listener.newFileDecrypted(newFile);
				ze = zipInput.getNextTarEntry();
				// SM.updateProgressBar(size, progress);

				

			}
			//zipInput.closeEntry();

			try {
				// http://stackoverflow.com/a/29681521/1989579
				zipInput.close();

			} catch (Exception e) {
				e.printStackTrace();
				if (!(e.getCause() instanceof BadPaddingException)) {
					throw e;
				}
			}

			in.close();

		} catch (Exception e) {
			// boolean error = false;
			// String mensaje;
			for (File f : outFileList) {
				try {
					FileUtils.delete(f);
				} catch (Exception ex) {
					// error = true;
					// mensaje = ex.getMessage();
				}
			}


       	 	String[] lparam = {inFile.getAbsolutePath()};
			throw new LockException(LockException.error_unlock0, lparam);
		}

	}

}
