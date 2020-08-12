package com.rodrigo.lock.core.tar;

import java.io.File;


public class TarUtils {
	/**
	 * Determines the tar file size of the given folder/file path
	 * 
	 * @param path
	 * @return
	 */
	public static long calculateTarSize(File path) {
		return tarSize(path) + TarConstants.EOF_BLOCK;
	}

	private static long tarSize(File dir) {
		long size = 0;

		if (dir.isFile()) {
			return entrySize(dir.length());
		} else {
			File[] subFiles = dir.listFiles();

			if (subFiles != null && subFiles.length > 0) {
				for (File file : subFiles) {
					if (file.isFile()) {
						size += entrySize(file.length());
					} else {
						size += tarSize(file);
					}
				}
			} else {
				// Empty folder header
				return TarConstants.HEADER_BLOCK;
			}
		}

		return size;
	}

	private static long entrySize(long fileSize) {
		long size = 0;
		size += TarConstants.HEADER_BLOCK; // Header
		size += fileSize; // File size

		long extra = size % TarConstants.DATA_BLOCK;

		if (extra > 0) {
			size += (TarConstants.DATA_BLOCK - extra); // pad
		}

		return size;
	}

	public static String trim(String s, char c) {
		StringBuffer tmp = new StringBuffer(s);
		for (int i = 0; i < tmp.length(); i++) {
			if (tmp.charAt(i) != c) {
				break;
			} else {
				tmp.deleteCharAt(i);
			}
		}

		for (int i = tmp.length() - 1; i >= 0; i--) {
			if (tmp.charAt(i) != c) {
				break;
			} else {
				tmp.deleteCharAt(i);
			}
		}

		return tmp.toString();
	}
}