package edu.columbia.psl.commons.compiler;

import java.io.*;
import java.lang.reflect.*;

/**
 * For compatibility with pre-1.4 JDKs, this class mimics
 */
public class CausedException extends Exception {
    private Throwable optionalCause = null;

    private static final Method INIT_CAUSE = initCauseMethod(); // Null for pre-1.4 JDKs.
    static Method initCauseMethod() {
        try {
            return Exception.class.getDeclaredMethod(
                "initCause",
                new Class[] { Throwable.class }
            );
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    public CausedException() {
    }

    public CausedException(String message) {
        super(message);
    }

    public CausedException(String message, Throwable optionalCause) {
        super(message);
        this.initCause(optionalCause);
    }

    public CausedException(Throwable optionalCause) {
        super(optionalCause == null ? null : optionalCause.getMessage());
        this.initCause(optionalCause);
    }

    public Throwable initCause(Throwable optionalCause) {
        if (CausedException.INIT_CAUSE == null) {
            this.optionalCause = optionalCause;
        } else
        {
            try {
                CausedException.INIT_CAUSE.invoke(this, new Object[] { optionalCause});
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Calling \"initCause()\"");
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Calling \"initCause()\"");
            } catch (InvocationTargetException e) {
                throw new RuntimeException("Calling \"initCause()\"");
            }
        }
        return this;
    }

    public Throwable getCause() {
        return this.optionalCause;
    }

    public void printStackTrace(PrintStream ps) {
        super.printStackTrace(ps);
        if (this.optionalCause == null) return;

        ps.print("Caused by: ");
        this.optionalCause.printStackTrace(ps);
    }
    public void printStackTrace(PrintWriter pw) {
        super.printStackTrace(pw);
        if (this.optionalCause == null) return;

        pw.print("Caused by: ");
        this.optionalCause.printStackTrace(pw);
    }
}
