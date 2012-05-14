
package edu.columbia.psl.commons.compiler;

import java.io.Serializable;

/**
 * Represents the location of a character in a document, as defined by an optional file name, a
 * line number and a column number.
 */
public class Location implements Serializable {
    public static final Location NOWHERE = new Location("<internally generated location>", (short) -1, (short) -1);

    private final String optionalFileName;
    private final short  lineNumber;
    private final short  columnNumber;

    /**
     * @param optionalFileName A human-readable indication where the document related to this
     *                         {@link Location} can be found
     */
    public Location(String optionalFileName, short lineNumber, short columnNumber) {
        this.optionalFileName = optionalFileName;
        this.lineNumber       = lineNumber;
        this.columnNumber     = columnNumber;
    }

    public String getFileName()     { return this.optionalFileName; }
    public short  getLineNumber()   { return this.lineNumber; }
    public short  getColumnNumber() { return this.columnNumber; }

    /**
     * Converts this {@link Location} into an english text, like<pre>
     * File Main.java, Line 23, Column 79</pre>
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        if (this.optionalFileName != null) {
            sb.append("File ").append(this.optionalFileName).append(", ");
        }
        sb.append("Line ").append(this.lineNumber).append(", ");
        sb.append("Column ").append(this.columnNumber);
        return sb.toString();
    }
}
