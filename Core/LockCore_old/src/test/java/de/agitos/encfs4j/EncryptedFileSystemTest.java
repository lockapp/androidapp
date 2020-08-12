package de.agitos.encfs4j;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.spi.FileSystemProvider;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.rodrigo.lock.core.CryptoHandlerUtils;
import com.rodrigo.lock.core.clases.DecryptListener;
import com.rodrigo.lock.core.clases.EncryptListener;
import com.rodrigo.lock.core.v1.DecryptHandler;
import com.rodrigo.lock.core.v1.EncryptHandler;
import com.rodrigo.lock.core.v2.DecryptHandlerV2;
import com.rodrigo.lock.core.v2.EncryptHandlerV2;
import com.rodrigo.lock.core.v2.clases.DecryptOptions;
import com.rodrigo.lock.core.v2.clases.EncryptOptions;

// FSTODO: test with AES/GCM/NoPadding (128-bit block cipher)

public class EncryptedFileSystemTest {

	private static final String patToWork ="D:\\Proyectos\\lock-github\\Core\\testFiles\\";
	private static final String toEncrypt ="toEncrypt";
	private static  String encrypted ;
	private static final String decrypted ="decrypted";
	
	private static List<File> origilanles=new LinkedList<File>();
	
	private static final String password="arroz";
	
	//con zip
	//testEncrypt: 6652937303 ns
	//testDecrypt: 4256530850 ns
	//usando tar
	//testEncrypt: 3386244636 ns
	//testDecrypt: 3214025063 ns



	
	@BeforeClass
	public static void init() throws IOException, URISyntaxException {		
		File folder = new File(patToWork + toEncrypt );
		for (File fileEntry : folder.listFiles()) {
			origilanles.add(fileEntry);
	    }			
	}


	
	@Test
	public void testEncrypt() throws Exception {

		long start = System.nanoTime();    

		EncryptHandlerV2 e = (EncryptHandlerV2) CryptoHandlerUtils.resolverAccion(origilanles);		
		EncryptOptions opciones = new EncryptOptions();
		opciones.setDejarCopiaSinBloquear(true);
		opciones.setPassword(password);
		opciones.setPathToSave(patToWork );
		opciones.setOutFileName("salida");
		
		e.init(opciones );
		encrypted = e.encrypt(new EcnriptListenerImpl());
		
		long elapsedTime = (System.nanoTime() - start) ;
		System.out.println("testEncrypt: "  + elapsedTime + " ns");
			
	}

	@Test
	public void testDecrypt() throws Exception {
		List<File> l = new LinkedList<>();
		File f = new File (encrypted);
		l.add(f);
		

		long start = System.nanoTime();    

		DecryptHandlerV2 e = (DecryptHandlerV2) CryptoHandlerUtils.resolverAccion(l);
		
		DecryptOptions opciones = new DecryptOptions();
		opciones.setConservarOriginal(true);
		opciones.setPassword("mal");
		opciones.setRutaSalida(patToWork+ decrypted );
		//opciones.setUsarVistaSegura(usarVistaSegura);
		e.init(opciones);
		
		e.decrypt(new DecryptListenerImpl());
		

		long elapsedTime = (System.nanoTime() - start);
		System.out.println("testDecrypt: "  + elapsedTime + " ns");
		
	}

	


	class EcnriptListenerImpl implements EncryptListener{
		@Override
		public void setNumberOfEncrypted(int i) {
			// TODO Auto-generated method stub
			
		}		
	}
	
	class DecryptListenerImpl implements DecryptListener{

		@Override
		public void setNumberOfFile(int i) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void newFileDecrypted(File f) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	

}
