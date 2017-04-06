package com.rodrigo.lock.core.data.constants;

public class EncryptedFileConstant {
	public final static int HEADER_VERSION_SIZE = 1;
	public final static String HEADER_VERSION ="00000001";
	public final static byte HEADER_VERSION_IN_BYTE =((byte) 0x01);

	/**
	 * formato del archivo
	 * 1 byte version | 80 byte clave  | 8 posicion de la structura | .......| structura
	 */

	public static final long START_OF_VERSION = 0;
	public static final long START_OF_KEY = 1;
	public static final long START_OF_INDEX_STRUCTURE= 81;
	public static final long START_OF_FILES= 89;

	public static Integer LONG_IN_BYTE  = 8;
	
}
