package com.rodrigo.lock.core.migration;

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

import com.rodrigo.lock.core.exceptions.LockException;
import com.rodrigo.lock.core.migration.crypto.CoreCrypto;
import com.rodrigo.lock.core.migration.crypto.Crypto;
import com.rodrigo.lock.core.utils.FileUtils;
import com.rodrigo.lock.core.utils.TextUtils;


public class DecryptHandler extends CryptoHandler {
	private long offset;

	public DecryptHandler(long offset, List<File> archivosIn, CryptoAction accion) {
		super(accion);
		this.offset = offset;
		inFile = archivosIn.get(0);
	}
	
	public File getInFile(){
		return inFile;
	}
	

	DecryptOptions opciones;
	//DecryptListener listener;
	File inFile;
	boolean inicializado = false;
	InputStream in;
	ZipEntry ze;
	ZipInputStream zipInput = null;

	boolean esExtraible = true;

	public void init(DecryptOptions opciones) throws Exception {
		this.opciones = opciones;

		if (!inicializado) {
			if (CryptoAction.TO_DECRYPT_WITH_IMAGE == accion) {
				initImage();
			} else {
				initSimple();
			}

			openCabezales();

			zipInput = new ZipInputStream(in);
			ze = zipInput.getNextEntry();
			// chequea si desbloqueo
			if (ze == null) {
				throw new LockException(LockException.error_password);
			}

			inicializado = true;
		}

	}

	private void initImage() throws Exception {
		try {
			in = new FileInputStream(inFile);
			in.skip(offset);
		} catch (Exception e) {
			String[] lparam = {this.inFile.getAbsolutePath()};
			throw new LockException(LockException.error_open, lparam);
		}
	}

	private void initSimple() throws Exception {
		try {
			in = new FileInputStream(inFile);
		} catch (Exception e) {
			String[] lparam = {this.inFile.getAbsolutePath()};
			throw new LockException(LockException.error_open, lparam);
		}
	}

	String pass;

	private void openCabezales() throws Exception {
		pass = opciones.getPassword();

		// se chequea la version
		byte[] version = new byte[1];
		in.read(version);
		if ((version[0] > ((byte) 0x01))) {
			throw new LockException(LockException.error_version);
		}

		// se empiezan a chequear los cabezales
		byte[] cavezalesActivos = new byte[1];
		in.read(cavezalesActivos);

		// vista segura
		if ((cavezalesActivos[0] & Byte.parseByte("00000100", 2)) == Byte
				.parseByte("00000000", 2)) {
			if (this.opciones.isUsarVistaSegura()) {
				throw new LockException(LockException.error_nosecureview);
			}
		}

		// caducidad
		if ((cavezalesActivos[0] & Byte.parseByte("00000001", 2)) == Byte
				.parseByte("00000001", 2)) {
			// cabezal.setCaducidad(true);
			byte[] caducidad = new byte[4];
			in.read(caducidad);
			int fechaCaducidad = FileUtils.byteArrayToInt(caducidad);

			Calendar c = Calendar.getInstance();
			SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
			String actualDate = df.format(c.getTime());

			if (Integer.valueOf(actualDate) > fechaCaducidad) {
				eliminarOriginal();
				throw new LockException(LockException.error_defeated);
				// eliminar archivo
			}
		}

		// cifrar
		if (((cavezalesActivos[0] & Byte.parseByte("00010000", 2)) == Byte
				.parseByte("00010000", 2)) || (version[0] == ((byte) 0x00))) {
			if (TextUtils.isEmpty(pass)) {
				throw new LockException(LockException.error_nosecureview);
			}

			// solo aca
			if ((cavezalesActivos[0] & Byte.parseByte("00000010", 2)) == Byte
					.parseByte("00000010", 2)) {
				// cabezal.setSoloAca(true);
				pass = this.opciones.mergeIdInPassword(this.opciones
						.getPassword());
			}

			CoreCrypto algo;
			if ((version[0] == ((byte) 0x00))) {
				algo = Crypto.createCryptoV0();   
			} else {
				algo = Crypto.createCryptoV1();   
			}
			in = new CipherInputStream(in, algo.getCiphertoDecZip(in, pass));

		}

		// prhoibir extraer
		if ((cavezalesActivos[0] & Byte.parseByte("00001000", 2)) == Byte
				.parseByte("00001000", 2)) {
			this.esExtraible = false;
		}

	}



	public void decrypt() throws Exception {
		//this.listener = listener;
		// this.SM.setIndeterminateProgressBar(idN);
		// se chequea si es imagen

		// se sacan los archivos todo es extraible
		//if (esExtraible) {
			extraerTodosLosArchivos();
		//} else {
		//	throw new LockException(LockException.error_noextract);
		//}

		// se elimina el original
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
				String fileName = ze.getName();
				File newFile = new File(this.opciones.getRutaSalida() + File.separator + fileName);

				if (newFile.exists()) {
					// throw new
					// Exception("Ya existe un archivo con el mismo nombre a desbloquear "
					// + newFile.getAbsolutePath());
					newFile = new File(FileUtils.createNewFileNameInPath(
							this.opciones.getRutaSalida() + File.separator,
							FileUtils.removeExtensionFile(fileName),
							FileUtils.getExtensionFile(fileName)));

				}

				// this.outFileList.add(new Archivo(newFile));
				outFileList.add(newFile);
				new File(newFile.getParent()).mkdirs();
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

				//this.listener.newFileDecrypted(newFile);
				ze = zipInput.getNextEntry();
				// SM.updateProgressBar(size, progress);

			}
			zipInput.closeEntry();

			try {
				// http://stackoverflow.com/a/29681521/1989579
				zipInput.close();
			} catch (Exception e) {
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


			String[] lparam = {this.inFile.getAbsolutePath()};
			throw new LockException(LockException.error_unlock0, lparam);
		}

	}
}
