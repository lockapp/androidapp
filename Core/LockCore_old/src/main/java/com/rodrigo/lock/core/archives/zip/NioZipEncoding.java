package com.rodrigo.lock.core.archives.zip;


import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;

/**
 * A ZipEncoding, which uses a java.nio {@link
 * java.nio.charset.Charset Charset} to encode names.
 *
 * <p>This implementation works for all cases under java-1.5 or
 * later. However, in java-1.4, some charsets don't have a java.nio
 * implementation, most notably the default ZIP encoding Cp437.</p>
 * 
 * <p>The methods of this class are reentrant.</p>
 * @Immutable
 */
class NioZipEncoding implements ZipEncoding {
    private final Charset charset;

    /**
     * Construct an NIO based zip encoding, which wraps the given
     * charset.
     * 
     * @param charset The NIO charset to wrap.
     */
    public NioZipEncoding(final Charset charset) {
        this.charset = charset;
    }

    /**
     * @see
     * org.apache.commons.compress.archivers.zip.ZipEncoding#canEncode(java.lang.String)
     */
    @Override
    public boolean canEncode(final String name) {
        final CharsetEncoder enc = this.charset.newEncoder();
        enc.onMalformedInput(CodingErrorAction.REPORT);
        enc.onUnmappableCharacter(CodingErrorAction.REPORT);

        return enc.canEncode(name);
    }

    /**
     * @see
     * org.apache.commons.compress.archivers.zip.ZipEncoding#encode(java.lang.String)
     */
    @Override
    public ByteBuffer encode(final String name) {
        final CharsetEncoder enc = this.charset.newEncoder();

        enc.onMalformedInput(CodingErrorAction.REPORT);
        enc.onUnmappableCharacter(CodingErrorAction.REPORT);

        final CharBuffer cb = CharBuffer.wrap(name);
        ByteBuffer out = ByteBuffer.allocate(name.length()
                                             + (name.length() + 1) / 2);

        while (cb.remaining() > 0) {
            final CoderResult res = enc.encode(cb, out,true);

            if (res.isUnmappable() || res.isMalformed()) {

                // write the unmappable characters in utf-16
                // pseudo-URL encoding style to ByteBuffer.
                if (res.length() * 6 > out.remaining()) {
                    out = ZipEncodingHelper.growBuffer(out, out.position()
                                                       + res.length() * 6);
                }

                for (int i=0; i<res.length(); ++i) {
                    ZipEncodingHelper.appendSurrogate(out,cb.get());
                }

            } else if (res.isOverflow()) {

                out = ZipEncodingHelper.growBuffer(out, 0);

            } else if (res.isUnderflow()) {

                enc.flush(out);
                break;

            }
        }

        out.limit(out.position());
        out.rewind();
        return out;
    }

    /**
     * @see
     * org.apache.commons.compress.archivers.zip.ZipEncoding#decode(byte[])
     */
    @Override
    public String decode(final byte[] data) throws IOException {
        return this.charset.newDecoder()
            .onMalformedInput(CodingErrorAction.REPORT)
            .onUnmappableCharacter(CodingErrorAction.REPORT)
            .decode(ByteBuffer.wrap(data)).toString();
    }
}