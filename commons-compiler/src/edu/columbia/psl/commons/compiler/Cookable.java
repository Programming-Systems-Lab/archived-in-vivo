
package edu.columbia.psl.commons.compiler;

import java.io.*;

/**
 * Base class for a simple {@link ICookable}.
 */
public abstract class Cookable implements ICookable {

    public abstract void cook(
        String optionalFileName,
        Reader r
    ) throws CompileException, IOException;

    public final void cook(Reader r) throws CompileException, IOException {
        this.cook(null, r);
    }

    public final void cook(InputStream is) throws CompileException, IOException {
        this.cook(null, is);
    }

    public final void cook(
        String      optionalFileName,
        InputStream is
    ) throws CompileException, IOException {
        this.cook(optionalFileName, is, null);
    }

    public final void cook(
        InputStream is,
        String      optionalEncoding
    ) throws CompileException, IOException {
        this.cook(optionalEncoding == null ? new InputStreamReader(is) : new InputStreamReader(is, optionalEncoding));
    }

    public final void cook(
        String      optionalFileName,
        InputStream is,
        String      optionalEncoding
    ) throws CompileException, IOException {
        this.cook(
            optionalFileName,
            optionalEncoding == null ? new InputStreamReader(is) : new InputStreamReader(is, optionalEncoding)
        );
    }

    public void cook(String s) throws CompileException {
        this.cook((String) null, s);
    }

    public void cook(
        String optionalFileName,
        String s
    ) throws CompileException {
        try {
            this.cook(optionalFileName, new StringReader(s));
        } catch (IOException ioe) {
            ioe.printStackTrace();
            throw new RuntimeException("SNO: StringReader throws IOException");
        }
    }

    public final void cookFile(File file) throws CompileException, IOException {
        this.cookFile(file, null);
    }

    public final void cookFile(
        File   file,
        String optionalEncoding
    ) throws CompileException, IOException {
        InputStream is = new FileInputStream(file);
        try {
            this.cook(
                file.getAbsolutePath(),
                optionalEncoding == null ? new InputStreamReader(is) : new InputStreamReader(is, optionalEncoding)
            );
            is.close();
            is = null;
        } finally {
            if (is != null) try { is.close(); } catch (IOException ex) { }
        }
    }

    public final void cookFile(String fileName) throws CompileException, IOException {
        this.cookFile(fileName, null);
    }

    public final void cookFile(
        String fileName,
        String optionalEncoding
    ) throws CompileException, IOException {
        this.cookFile(new File(fileName), optionalEncoding);
    }

    /**
     * Reads all characters from the given {@link Reader} into a {@link String}.
     */
    public static String readString(Reader r) throws IOException {
        StringBuffer sb = new StringBuffer();
        char[] ca = new char[4096];
        for (;;) {
            int count = r.read(ca);
            if (count == -1) break;
            sb.append(ca, 0, count);
        }
        String s = sb.toString();
        return s;
    }
}
