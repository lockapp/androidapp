package com.rodrigo.lock.core.utils;

import java.io.File;
import java.util.List;

import com.rodrigo.lock.core.datatype.INewFile;

public class PendienteUtils {

	public static void addNamesDataFile(List<String> pendintes, List<INewFile> files){
		for (INewFile file:files){
			pendintes.add(file.getName());
		}
	}
	

	public static void addNamesFile(List<String> pendintes, List<File> files){
		for (File file:files){
			pendintes.add(file.getName());
		}
	}
	
	
	public static void removeNamesDataFile(List<String> pendintes, List<INewFile> files){
		for (INewFile file:files){
			pendintes.remove(file.getName());
		}
	}
	

	public static void removeNamesFile(List<String> pendintes, List<File> files){
		for (File file:files){
			pendintes.remove(file.getName());
		}
	}
	
}
