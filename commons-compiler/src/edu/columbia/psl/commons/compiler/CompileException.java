
package edu.columbia.psl.commons.compiler;

/**
 * An exception that reflects an error during compilation.
 */
public class CompileException extends LocatedException {

    public CompileException(String message, Location optionalLocation) {
        super(message, optionalLocation);
    }
    public CompileException(String message, Location optionalLocation, Throwable cause) {
        super(message, optionalLocation, cause);
    }
}
