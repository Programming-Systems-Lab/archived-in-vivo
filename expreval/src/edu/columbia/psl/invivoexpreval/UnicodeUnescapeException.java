


package edu.columbia.psl.invivoexpreval;

/**
 * Represents a problem that occurred while unescaping a unicode escape
 * sequence through a {@link edu.columbia.psl.invivoexpreval.UnicodeUnescapeReader}.
 */
public class UnicodeUnescapeException extends RuntimeException {
    public UnicodeUnescapeException(String message) {
        super(message);
    }
}
