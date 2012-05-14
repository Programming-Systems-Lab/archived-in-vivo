package edu.columbia.psl.invivoexpreval;

public class InVivoRuntimeException extends RuntimeException {
    public InVivoRuntimeException()                            {                    }
    public InVivoRuntimeException(String message)              { super(message);    }
    public InVivoRuntimeException(String message, Throwable t) { super(message, t); }
}
