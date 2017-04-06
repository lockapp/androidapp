package com.rodrigo.lock.core.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class LockFileInpuStream extends InputStream {

	//boolean hasHitEOF;
	InputStream file;

	long entryOffset;
	long entrySize;
	
	public LockFileInpuStream(FileInputStream file, long posOfStart, long size)
			throws IOException {

		//hasHitEOF = false;
		entryOffset=0;
		entrySize =size;
		
		file.skip(posOfStart);
		//this.file = new BufferedInputStream(file);
		this.file = (file);
		
	}

	@Override
	public int read() throws IOException {
		if ( available() <=0) {
			return -1;
		}
		entryOffset++;
		return file.read();
	}

	@Override
	public int read(byte b[]) throws IOException {
		return read(b, 0, b.length);
	}

    @Override
    public int read(final byte[] buf, final int offset, int numToRead) throws IOException {
    	int totalRead = 0;

        if (/*hasHitEOF || isDirectory() || */entryOffset >= entrySize) {
            return -1;
        }
        numToRead = Math.min(numToRead, available());        
        totalRead = file.read(buf, offset, numToRead);
        
        if (totalRead == -1) {
            if (numToRead > 0) {
                throw new IOException("Truncated file");
            }
            //hasHitEOF = true;
            //se cambio fin por 
            entryOffset =entrySize;
        } else {
            //count(totalRead);
            entryOffset += totalRead;
        }

        return totalRead;
    }

	
	@Override
	public long skip(long n) throws IOException {
		entryOffset += n;
		return file.skip(n);
	}

	
    @Override
    public int available() throws IOException {
        if (entrySize - entryOffset > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        return (int) (entrySize - entryOffset);
    }

	

	@Override
	public void close() throws IOException {
		//try{
			file.close();		 	
		//http://stackoverflow.com/a/29681521/1989579
		/*}catch(Exception e){            	
        	if (! (e.getCause() instanceof BadPaddingException)){
        		throw e;
        	}
        }*/
		
	}
	
	
	@Override
	public boolean markSupported() {
		return false;
	}

}
