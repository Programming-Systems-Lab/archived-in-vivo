


package edu.columbia.psl.invivoexpreval.util;

import java.io.*;

/**
 * A {@link java.io.FilterReader} that copies the bytes being passed through
 * to a given {@link java.io.Writer}. This is in analogy with the UNIX "tee" command.
 */
public class TeeReader extends FilterReader {
    private final Writer  out;
    private final boolean closeWriterOnEOF;

    public TeeReader(Reader in, Writer out, boolean closeWriterOnEOF) {
        super(in);
        this.out              = out;
        this.closeWriterOnEOF = closeWriterOnEOF;
    }
    public void close() throws IOException {
        this.in.close();
        this.out.close();
    }
    public int read() throws IOException {
        int c = this.in.read();
        if (c == -1) {
            if (this.closeWriterOnEOF) {
                this.out.close();
            } else {
                this.out.flush();
            }
        } else {
            this.out.write(c);
        }
        return c;
    }
    public int read(char[] cbuf, int off, int len) throws IOException {
        int bytesRead = this.in.read(cbuf, off, len);
        if (bytesRead == -1) {
            if (this.closeWriterOnEOF) {
                this.out.close();
            } else {
                this.out.flush();
            }
        } else {
            this.out.write(cbuf, off, bytesRead);
        }
        return bytesRead;
    }
}
