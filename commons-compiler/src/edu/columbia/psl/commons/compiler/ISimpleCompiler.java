
package edu.columbia.psl.commons.compiler;

/**
 * A simplified Java&trade; compiler that can compile only a single compilation unit. (A "compilation unit" is the
 * document stored in a ".java" file.)
 * <p>
 * Opposed to a normal ".java" file, you can declare multiple public classes here.
 * <p>
 * To set up an {@link ISimpleCompiler} object, proceed as follows:
 * <ol>
 *   <li>
 *   Create an {@link ISimpleCompiler}-implementing object
 *   <li>
 *   Optionally set an alternate parent class loader through {@link #setParentClassLoader(ClassLoader)}.
 *   <li>
 *   Call any of the {@link ICookable#cook(String, Reader)} methods to scan, parse, compile and load the compilation
 *   unit into the JVM.
 *   <li>
 *   Call {@link #getClassLoader()} to obtain a {@link ClassLoader} that you can use to access the compiled classes.
 * </ol>
 */
public interface ISimpleCompiler extends ICookable {

    /**
     * Returns a {@link ClassLoader} object through which the previously compiled classes can be accessed. This {@link
     * ClassLoader} can be used for subsequent {@link ISimpleCompiler}s in order to compile compilation units that use
     * types (e.g. declare derived types) declared in the previous one.
     * <p>
     * This method must only be called after exactly on of the {@link #cook(String, java.io.Reader)} methods was called.
     */
    ClassLoader getClassLoader();

}
