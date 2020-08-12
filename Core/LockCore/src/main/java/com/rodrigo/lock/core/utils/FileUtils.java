package com.rodrigo.lock.core.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import com.rodrigo.lock.core.data.constants.EncryptedFileConstant;

public class FileUtils {
	public static final String LOCK_EXTENSION = "lock";
	public static final String TEMP_EXTENSION = "temp";

	public static void getFileAndSubFiles(final Collection<File> files, final File actual) {		
		if (actual.isDirectory()){
			for (File hijo:  actual.listFiles()){
				getFileAndSubFiles(files, hijo);
			}
		}else{
			files.add(actual);
		}
	}

	public static byte[] longToBytes(long x) {
		ByteBuffer buffer = ByteBuffer
				.allocate(EncryptedFileConstant.LONG_IN_BYTE);
		buffer.putLong(x);
		return buffer.array();
	}

	public static long bytesToLong(byte[] bytes) {
		ByteBuffer buffer = ByteBuffer
				.allocate(EncryptedFileConstant.LONG_IN_BYTE);
		buffer.put(bytes);
		buffer.flip();// need flip
		return buffer.getLong();
	}

	// read toByteArray
	// -----------------------------------------------------------------------

	/**
	 * Gets the contents of an <code>InputStream</code> as a <code>byte[]</code>
	 * .
	 * <p>
	 * This method buffers the input internally, so there is no need to use a
	 * <code>BufferedInputStream</code>.
	 *
	 * @param input
	 *            the <code>InputStream</code> to read from
	 * @return the requested byte array
	 * @throws NullPointerException
	 *             if the input is null
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	public static byte[] toByteArray(InputStream input) throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		copy(input, output);
		return output.toByteArray();

	}

	public static final int BUFFER_SIZE = 8192; // 8192, 2048
	// public static byte[] createBuffer(){
	// return new byte[BUFFER_SIZE];
	// }

	public static void copy(InputStream from, OutputStream to)
			throws IOException {
	    //long total = 0;
		int count;
		byte data[] = new byte[BUFFER_SIZE];
		while ((count = from.read(data, 0, BUFFER_SIZE)) != -1) {
			to.write(data, 0, count);
			//total = total + count;
		}
		//return total;
	}

	public static long copy(InputStream from, OutputStream to, long sizeOfByte)
			throws IOException {
		long total = 0;
		byte[] data = new byte[BUFFER_SIZE];

		while (true) {
			int toRead = (int) ((total + BUFFER_SIZE > sizeOfByte) ? (sizeOfByte - total)
					: BUFFER_SIZE);

			int r = from.read(data, 0, toRead);
			if (r == -1) {
				break;
			}
			to.write(data, 0, r);
			total = total + r;
			if (total == sizeOfByte) {
				break;
			}
		}
		return total;
	}

	public static String getNameRelativeToBase(File baseF, File node) {
		String fileName = node.getAbsoluteFile().toString();
		int x = baseF.getParent().length() + 1;
		int y = fileName.length();
		return fileName.substring(x, y);
	}

	public static String createNewFileNameInPath(String path, String name,
			String extension) {
		int i = 0;
		if (!TextUtils.isEmpty(extension)) {
			extension = "." + extension;
		}
		String iter = "";
		String newF = path + name + extension;

		while (new File(path + name + iter + extension).exists()) {
			i++;
			iter = " (" + i + ")";
		}
		return path + name + iter + extension;
	}

	public static long getSize(List<File> inFileList) {
		long size = 0;
		for (File a : inFileList) {
			size += getSize(a);
		}
		return size;
	}

	public static long getSize(File file) {
		if (file.isDirectory()) {
			long length = 0;
			for (File iter : file.listFiles()) {
				length += getSize(iter);
			}
			return length;
		} else {
			return file.length();
		}
	}

	public static String sizeToString(long tamF) {
		DecimalFormat formatter = new DecimalFormat("#########.##");
		String fileSize = String.valueOf(tamF) + " B";

		double aux = tamF;
		aux = aux / 1024;

		if (aux > 1) {
			fileSize = String.valueOf(formatter.format(aux)) + " kB";
		}
		aux = aux / 1024;
		if (aux > 1) {
			fileSize = String.valueOf(formatter.format(aux)) + " MB";
		}
		aux = aux / 1024;
		if (aux > 1) {
			fileSize = String.valueOf(formatter.format(aux)) + " GB";
		}
		return fileSize;
	}

	public static String getExtensionFile(String fileName) {
		String extension = "";

		int i = fileName.lastIndexOf('.');
		if (i > 0) {
			extension = fileName.substring(i + 1);
		}
		return extension.toLowerCase();
	}

	public static String removeExtensionFile(String fileName) {
		int i = fileName.lastIndexOf('.');
		if (i > 0) {
			return fileName.substring(0, i);
		} else {
			return fileName;
		}

	}

	public static String getTempPathFileForFile(File file) {
		String basePath = file.getParent() + File.separator;
		String name =  FileUtils.removeExtensionFile(file.getName());
		String extension = FileUtils.getExtensionFile(file.getName());

		if (!TextUtils.isEmpty(extension)) {
			extension = "." + extension;
		}
		extension= extension + "." + FileUtils.TEMP_EXTENSION;
		Integer digits = getRandomDigit();
		while (new File(basePath + name + "_" + digits + extension).exists()) {
			digits++;
		}

		return basePath + name + "_" + digits + extension;

	}

	private static Integer getRandomDigit() {
		Random rand = new Random();
		return 0 + rand.nextInt((9999999 - 0) + 1);
	}

	/***
	 * deprecated usado para la version vieja
	 * 
	 * @param b
	 * @return
	 */
	public static int byteArrayToInt(byte[] b) {
		return b[3] & 0xFF | (b[2] & 0xFF) << 8 | (b[1] & 0xFF) << 16
				| (b[0] & 0xFF) << 24;
	}

	public static void delete(File file) throws IOException {

		if (file.isDirectory()) {
			// directory is empty, then delete it
			if (file.list().length == 0) {
				file.delete();
				// System.out.println("Directory is deleted : " +
				// file.getAbsolutePath());
			} else {
				// list all the directory contents
				String files[] = file.list();
				for (String temp : files) {
					// construct the file structure
					File fileDelete = new File(file, temp);
					// recursive delete
					delete(fileDelete);
				}

				// checkAndInit the directory again, if empty then delete it
				if (file.list().length == 0) {
					file.delete();
					// System.out.println("Directory is deleted : " +
					// file.getAbsolutePath());
				}
			}

		} else {
			// if file, then delete it
			file.delete();
			// System.out.println("File is deleted : " +
			// file.getAbsolutePath());
		}
	}

	public static void delete(List<File> files) throws IOException {
		for (File f : files) {
			delete(f);
		}
	}

	public static int countFileNodes(List<File> inFileList) {
		int count = 0;
		for (File a : inFileList) {
			count = count + countFileNodes(a);
		}
		return count;

	}

	public static int countFileNodes(File f) {
		if (f == null) {
			return 0;
		}
		int countFiles = 1;
		if (f.isDirectory()) {
			File[] files = f.listFiles();
			if (files != null) {
				for (File iter : files) {
					countFiles = countFiles + countFileNodes(iter);
				}
			}
		}
		return countFiles;
	}

	public static boolean esBobeda(File file) {
		if (!file.exists()) {
			return false;
		}
		if (file.isDirectory()) {
			return false;
		}
		String extension = FileUtils.getExtensionFile(file.getName());
		return FileUtils.isLockExtension(extension);
	}

	public static boolean isLockExtension(String extension) {
		return LOCK_EXTENSION.equals(extension);
	}

	public static String createNameForFile() {
		String name = "lockFile";

		Calendar c = Calendar.getInstance();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hhmm ");
		String actualDate = df.format(c.getTime());
		name = actualDate + name;

		return name;

	}

}
