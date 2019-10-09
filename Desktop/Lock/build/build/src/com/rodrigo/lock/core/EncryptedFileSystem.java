package com.rodrigo.lock.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.rodrigo.lock.core.clases.FileSystemStructure;
import com.rodrigo.lock.core.clases.FileType;
import com.rodrigo.lock.core.clases.LockFile;
import com.rodrigo.lock.core.data.EncryptedFileSystemDAO;
import com.rodrigo.lock.core.datatype.AddFileListener;
import com.rodrigo.lock.core.datatype.INewFile;
import com.rodrigo.lock.core.exceptions.LockException;
import com.rodrigo.lock.core.utils.FileUtils;
import com.rodrigo.lock.core.utils.PendienteUtils;

/**
 * clase que debe ser unica por bobeda. Gesitona la bobeda y la concurrencia
 * la idea es que solo un hilo pueda escribir en un archivo temporal (se gestiona a trabes de los syncronized
 * y el archivo se principal se gestiona a trabes de lock de escritura y lectura
 * 
 * @author Rodrigo
 *
 */
public class EncryptedFileSystem {

	private final ReadWriteLock lockForOrignial = new ReentrantReadWriteLock();
	private final Object lockForTemp = new Object();

	private final EncryptedFileSystemDAO dao;
	private List<String> pendientesDeAgregar = Collections.synchronizedList(new ArrayList());

	private String password;

	private EncryptedFileSystem(EncryptedFileSystemDAO dao, String password) {
		this.dao = dao;
		this.password = password;
	}

	static EncryptedFileSystem createEncryptedFile(String filePath,	String password) {
		File file = new File(filePath);
		if (file.exists()) {
			throw new LockException(LockException.el_archivo_ya_existe);
		}
		file.getParentFile().mkdirs();

		EncryptedFileSystemDAO dao = new EncryptedFileSystemDAO(filePath);
		dao.writeVersion();
		dao.createKey(password);
		dao.initStructure();

		EncryptedFileSystem system = new EncryptedFileSystem(dao, password);
		return system;
	}

	static EncryptedFileSystem openEncryptedFile(String filePath,String password) {
		File file = new File(filePath);
		if (!file.exists()) {
			throw new LockException(LockException.not_found);
		}

		EncryptedFileSystemDAO dao = new EncryptedFileSystemDAO(filePath);
		dao.checkVersion();
		dao.loadKey(password);
		dao.loadStructure();

		EncryptedFileSystem system = new EncryptedFileSystem(dao, password);
		return system;
	}

	public void addFilesWithPreview(AddFileListener listener,List<INewFile> files) {
		PendienteUtils.addNamesDataFile(pendientesDeAgregar, files);
		synchronized (lockForTemp) {
			try {
				beginTransactionToWriteInTemp();
				for (INewFile file : files) {
					LockFile lockFile =dao.addEncryptedFileFromFileToTemp(file.getIn(), file.getName());
					InputStream preview = file.getPreview();
					if (preview!= null){
						dao.addPreviewToTemp(lockFile, preview);
					}
				}
				commitTransactionInTemp();
			} catch (LockException e) {
				abortTransactionInTemp();
				throw e;
			} catch (Exception e) {
				abortTransactionInTemp();
				throw new LockException(LockException.no_se_pudo_agregar_el_archivo);
			}
		}
		PendienteUtils.removeNamesDataFile(pendientesDeAgregar, files);

	}

	public void addFile(AddFileListener listener, List<File> files) {
		PendienteUtils.addNamesFile(pendientesDeAgregar, files);
		synchronized (lockForTemp) {
			try {
				beginTransactionToWriteInTemp();
				for (File file : files) {
					if (file.isDirectory()) {
						generateFileListForFolder(file, file, listener);
					} else {
						FileInputStream input = new FileInputStream(file);
						LockFile lf = dao.addEncryptedFileFromFileToTemp(input,
								file.getName());
						listener.fileAdded();
					}
				}
				commitTransactionInTemp();
			} catch (LockException e) {
				abortTransactionInTemp();
				throw e;
			} catch (Exception e) {
				abortTransactionInTemp();
				throw new LockException(LockException.no_se_pudo_agregar_el_archivo);
			}
		}
		PendienteUtils.removeNamesFile(pendientesDeAgregar, files);
	}

	private void generateFileListForFolder(File baseFolder, File node,	AddFileListener listener) throws FileNotFoundException {
		if (node.isFile()) {
			FileInputStream input = new FileInputStream(node);
			dao.addEncryptedFileFromFileToTemp(input,
					FileUtils.getNameRelativeToBase(baseFolder, node));
			listener.fileAdded();
		}
		if (node.isDirectory()) {
			dao.addFolderToTemp(FileUtils.getNameRelativeToBase(baseFolder,
					node));
			listener.fileAdded();
			String[] subNote = node.list();
			for (String filename : subNote) {
				generateFileListForFolder(baseFolder, new File(node, filename),
						listener);
			}
		}

	}

	public void deleteFiles(String... ids) throws Exception {
		synchronized (lockForTemp) {
			try {			
				beginTransactionToWriteInTempSinCopiar();
				
				Set<String> folders = new LinkedHashSet<String>();
				Set<String> files = new LinkedHashSet<String>();
	
				FileSystemStructure structure = dao.getTempStructure();
				for (String fileId : ids) {
					if (structure.getFiles().containsKey(fileId)) {
						LockFile f = structure.getFiles().get(fileId);
						if (f.getType() == FileType.FOLDER) {
							folders.add(fileId);
						} else {
							files.add(fileId);
							if (f.getPreviewId() != null) {
								files.add(f.getPreviewId());
							}
						}
					}
				}
			
				dao.delteFoldersInTempStructure(folders);
				dao.delteFilesInTemp(files);
				commitTransactionInTemp();
			} catch (LockException e) {
				abortTransactionInTemp();
				throw e;
			} catch (Exception e) {
				abortTransactionInTemp();
				throw new LockException(LockException.no_se_pudo_eliminar_el_archivo);
			}
		}
	}

	public void extractFile(String id, OutputStream out) {
		lockForOrignial.readLock().lock();
		try {
			dao.extractFile(id, out);
		} finally {
			lockForOrignial.readLock().unlock();
		}
	}

	public void extractAllFilesAndFolders(File baseFolder)throws FileNotFoundException {
		lockForOrignial.readLock().lock();
		try {
			List<LockFile> files = new ArrayList<LockFile>(dao.getStructure().getFiles().values());
			for (LockFile lf : files) {
				if (lf.getType() != FileType.PREVIEW) {
					File file = new File(baseFolder.getAbsoluteFile()+ File.separator + lf.getFullPath());
					if (lf.getType() == FileType.FOLDER) {
						file.mkdirs();
					} else {
						if (file.exists()) {
							file = new File(FileUtils.createNewFileNameInPath(file.getParent() + File.separator,FileUtils.removeExtensionFile(file.getName()), FileUtils.getExtensionFile(file.getName())));
						}
						file.getParentFile().mkdirs();
						FileOutputStream out = new FileOutputStream(file);
						dao.extractFile(lf.getId(), out);
					}
				}
			}
		} finally {
			lockForOrignial.readLock().unlock();
		}
	}

	
	
	public List<LockFile> getFilesAndFolders() {
		lockForOrignial.readLock().lock();
		try {
			List<LockFile> files = new ArrayList<LockFile>(dao.getStructure().getFiles().values());
			Iterator<LockFile> iter = files.iterator();
			while (iter.hasNext()) {
				LockFile lf = iter.next();
				if (lf.getType() == FileType.PREVIEW) {
					iter.remove();
				}
			}
			return files;

		} finally {
			lockForOrignial.readLock().unlock();
		}
	}

	public LockFile getFile(String id) {
		lockForOrignial.readLock().lock();
		try {
			return dao.getStructure().getFiles().get(id);
		} finally {
			lockForOrignial.readLock().unlock();
		}
	}

	
	/**
	 * metodo que retorna el id del preview de un archivo, -1 en caso de no
	 * existir preview
	 * 
	 * @param id
	 * @return retorna el id del preview de un archivo, -1 en caso de no existir
	 *         preview
	 */
	public String getPreviewIdOfFile(String id) {
		lockForOrignial.readLock().lock();
		try {
			if (!dao.getStructure().getFiles().containsKey(id)) {
				return null;
			}
			LockFile lf = dao.getStructure().getFiles().get(id);
			if (!dao.getStructure().getFiles().containsKey(lf.getPreviewId())) {
				return null;
			}
			return lf.getPreviewId();
		} finally {
			lockForOrignial.readLock().unlock();
		}
	}

	public boolean equalPassword(String pass) {
		return this.password.equals(pass);
	}

	/**
	 * copia el contenido del archivo a uno temporal
	 * 
	 * @throws Exception
	 */
	private void beginTransactionToWriteInTemp() throws Exception {
		lockForOrignial.readLock().lock();
		try {
			dao.initTempFilePath();
			dao.copyActualContentToTempFile();
			dao.copyActualStructureToTempStructure();
		} finally {
			lockForOrignial.readLock().unlock();
		}
	}


	/**
	 * copia el contenido del archivo a uno temporal sin copiar el contendio del archivo
	 * 
	 * @throws Exception
	 */
	private void beginTransactionToWriteInTempSinCopiar() throws Exception {
		lockForOrignial.readLock().lock();
		try {		
			dao.initTempFilePath();
			dao.copyActualStructureToTempStructure();
		} finally {
			lockForOrignial.readLock().unlock();
		}
	}
	/**
	 * copia el contenido del temporal en el original
	 * 
	 * @throws Exception
	 */
	public void commitTransactionInTemp() throws Exception {
		lockForOrignial.writeLock().lock();
		try {
			dao.writeStructureInTempFile();
			dao.convertTempFileInOriginalFile();
			dao.convertTempStructureInOriginal();
		} finally {
			lockForOrignial.writeLock().unlock();
		}
	}

	/**
	 * elimina el archivo temporal
	 * 
	 * @throws Exception
	 */
	private void abortTransactionInTemp() {
		try {
			dao.deleteTempFile();
		} catch (Exception e) {
			// TODO Auto-generated catch block
		}
	}
}
