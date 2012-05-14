


package edu.columbia.psl.invivoexpreval;

import java.io.*;

/**
 * A {@link FilterReader} that unescapes the "Unicode Escapes"
 * as described in
 * <a href="http://java.sun.com/docs/books/jls/second_edition/html/lexical.doc.html#100850">the
 * Java Language Specification, 2nd edition</a>.
 * <p>
 * Notice that it is possible to formulate invalid escape sequences, e.g.
 * "&#92;u123g" ("g" is not a valid hex character). This is handled by
 * throwing a {@link java.lang.RuntimeException}-derived
 * {@link edu.columbia.psl.invivoexpreval.UnicodeUnescapeException}.
 */
public class UnicodeUnescapeReader extends FilterReader {

    /**
     * @param in
     */
    public UnicodeUnescapeReader(Reader in) {
        super(in);
    }

    /**
     * Override {@link FilterReader#read()}.
     *
     * @throws UnicodeUnescapeException Invalid escape sequence encountered
     */
    public int read() throws IOException {
        int c;

        // Read next character.
        if (this.unreadChar == -1) {
            c = this.in.read();
        } else {
            c = this.unreadChar;
            this.unreadChar = -1;
        }

        // Check for backslash-u escape sequence, preceeded with an even number
        // of backslashes.
        if (c != '\\' || this.oddPrecedingBackslashes) {
            this.oddPrecedingBackslashes = false;
            return c;
        }

        // Read one character ahead and check if it is a "u".
        c = this.in.read();
        if (c != 'u') {
            this.unreadChar = c;
            this.oddPrecedingBackslashes = true;
            return '\\';
        }

        // Skip redundant "u"s.
        do {
            c = this.in.read();
            if (c == -1) throw new UnicodeUnescapeException("Incomplete escape sequence");
        } while (c == 'u');

        // Decode escape sequence.
        char[] ca = new char[4];
        ca[0] = (char) c;
        if (this.in.read(ca, 1, 3) != 3) throw new UnicodeUnescapeException("Incomplete escape sequence");
        try {
            return 0xffff & Integer.parseInt(new String(ca), 16);
        } catch (NumberFormatException ex) {
            throw new UnicodeUnescapeException("Invalid escape sequence \"\\u" + new String(ca) + "\"");
        }
    }

    /**
     * Override {@link FilterReader#read(char[], int, int)}.
     */
    public int read(char[] cbuf, int off, int len) throws IOException {
        if (len == 0) return 0;
        int res = 0;
        do {
            int c = this.read();
            if (c == -1) break;
            cbuf[off++] = (char) c;
        } while (++res < len);
        return res == 0 ? -1 : res;
    }

    /**
     * Simple unit testing.
     */
    public static void main(String[] args) throws IOException {
        Reader r = new UnicodeUnescapeReader(new StringReader(args[0]));
        for (;;) {
            int c = r.read();
            if (c == -1) break;
            System.out.print((char) c);
        }
        System.out.println();
    }

    private int     unreadChar = -1; // -1 == none
    private boolean oddPrecedingBackslashes = false;
}
