
package edu.columbia.psl.commons.compiler;

import java.io.*;

/**
 * "Cooking" means scanning a sequence of characters and turning them into some
 * JVM-executable artifact.
 */
public interface ICookable {

    /**
     * The {@link ClassLoader} that loads this classes on the boot class path, i.e. the JARs in the JRE's "lib" and
     * "lib/ext" directories, but not the JARs and class directories specified through the class path.
     */
    ClassLoader BOOT_CLASS_LOADER = ClassLoader.getSystemClassLoader().getParent();

    /**
     * The "parent class loader" is used to load referenced classes. Useful values are:
     * <table border="1"><tr>
     *   <td><code>System.getSystemClassLoader()</code></td>
     *   <td>The running JVM's class path</td>
     * </tr><tr>
     *   <td><code>Thread.currentThread().getContextClassLoader()</code> or <code>null</code></td>
     *   <td>The class loader effective for the invoking thread</td>
     * </tr><tr>
     *   <td>{@link #BOOT_CLASS_LOADER}</td>
     *   <td>The running JVM's boot class path</td>
     * </tr></table>
     * The parent class loader defaults to the current thread's context class loader.
     */
    void setParentClassLoader(ClassLoader optionalParentClassLoader);

    String SYSTEM_PROPERTY_SOURCE_DEBUGGING_ENABLE = "";

    String SYSTEM_PROPERTY_SOURCE_DEBUGGING_DIR = "";

    void setDebuggingInformation(boolean debugSource, boolean debugLines, boolean debugVars);

    /**
     * Reads, scans, parses and compiles Java tokens from the given {@link Reader}.
     *
     * @param optionalFileName Used when reporting errors and warnings.
     */
    void cook(String optionalFileName, Reader r) throws CompileException, IOException;

    /**
     * Reads, scans, parses and compiles Java tokens from the given {@link Reader}.
     */
    void cook(Reader r) throws CompileException, IOException;

    /**
     * Reads, scans, parses and compiles Java tokens from the given {@link InputStream}, encoded
     * in the "platform default encoding".
     */
    void cook(InputStream is) throws CompileException, IOException;

    /**
     * Reads, scans, parses and compiles Java tokens from the given {@link InputStream}, encoded
     * in the "platform default encoding".
     *
     * @param optionalFileName Used when reporting errors and warnings.
     */
    void cook(
        String      optionalFileName,
        InputStream is
    ) throws CompileException, IOException;

    /**
     * Reads, scans, parses and compiles Java tokens from the given {@link InputStream} with the
     * given <code>encoding</code>.
     */
    void cook(
        InputStream is,
        String      optionalEncoding
    ) throws CompileException, IOException;

    /**
     * Reads, scans, parses and compiles Java tokens from the given {@link InputStream} with the
     * given <code>encoding</code>.
     *
     * @param optionalFileName Used when reporting errors and warnings.
     */
    void cook(
        String      optionalFileName,
        InputStream is,
        String      optionalEncoding
    ) throws CompileException, IOException;

    /**
     * Reads, scans, parses and compiles Java tokens from the given {@link String}.
     */
    void cook(String s) throws CompileException;

    /**
     * Reads, scans, parses and compiles Java tokens from the given {@link String}.
     *
     * @param optionalFileName Used when reporting errors and warnings.
     */
    void cook(String optionalFileName, String s) throws CompileException;

    /**
     * Reads, scans, parses and compiles Java tokens from the given {@link File}, encoded
     * in the "platform default encoding".
     */
    void cookFile(File file) throws CompileException, IOException;

    /**
     * Reads, scans, parses and compiles Java tokens from the given {@link File} with the
     * given <code>encoding</code>.
     */
    void cookFile(
        File   file,
        String optionalEncoding
    ) throws CompileException, IOException;

    /**
     * Reads, scans, parses and compiles Java tokens from the named file, encoded in the "platform
     * default encoding".
     */
    void cookFile(String fileName) throws CompileException, IOException;

    /**
     * Reads, scans, parses and compiles Java tokens from the named file with the given
     * <code>encoding</code>.
     */
    void cookFile(
        String fileName,
        String optionalEncoding
    ) throws CompileException, IOException;
}
