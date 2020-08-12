package com.rodrigo.lock.core.archives.zip;


import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * A fallback ZipEncoding, which uses a java.io means to encode names.
 *
 * <p>This implementation is not suitable for encodings other than
 * UTF-8, because java.io encodes unmappable character as question
 * marks leading to unreadable ZIP entries on some operating
 * systems.</p>
 * 
 * <p>Furthermore this implementation is unable to tell whether a
 * given name can be safely encoded or not.</p>
 * 
 * <p>This implementation acts as a last resort implementation, when
 * neither {@link Simple8BitZipEnoding} nor {@link NioZipEncoding} is
 * available.</p>
 * 
 * <p>The methods of this class are reentrant.</p>
 * @Immutable
 */
class FallbackZipEncoding implements ZipEncoding {
    private final String charsetName;

    /**
     * Construct a fallback zip encoding, which uses the platform's
     * default charset.
     */
    public FallbackZipEncoding() {
        this.charsetName = null;
    }

    /**
     * Construct a fallback zip encoding, which uses the given charset.
     * 
     * @param charsetName The name of the charset or {@code null} for
     *                the platform's default character set.
     */
    public FallbackZipEncoding(final String charsetName) {
        this.charsetName = charsetName;
    }

    /**
     * @see
     * org.apache.commons.compress.archivers.zip.ZipEncoding#canEncode(java.lang.String)
     */
    @Override
    public boolean canEncode(final String name) {
        return true;
    }

    /**
     * @see
     * org.apache.commons.compress.archivers.zip.ZipEncoding#encode(java.lang.String)
     */
    @Override
    public ByteBuffer encode(final String name) throws IOException {
        if (this.charsetName == null) { // i.e. use default charset, see no-args constructor
            return ByteBuffer.wrap(name.getBytes());
        }
        return ByteBuffer.wrap(name.getBytes(this.charsetName));
    }

    /**
     * @see
     * org.apache.commons.compress.archivers.zip.ZipEncoding#decode(byte[])
     */
    @Override
    public String decode(final byte[] data) throws IOException {
        if (this.charsetName == null) { // i.e. use default charset, see no-args constructor
            return new String(data);
        }
        return new String(data,this.charsetName);
    }
}