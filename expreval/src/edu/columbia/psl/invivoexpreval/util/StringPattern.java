


package edu.columbia.psl.invivoexpreval.util;

import java.util.*;

/**
 * Implementation of a UNIX shell-like string pattern algorithm.
 * <p>
 * Additionally, the concept of the "combined pattern" is supported (see
 * {@link #matches(StringPattern[], String)}.
 */
public class StringPattern {

    /**
     * @see #matches(StringPattern[], String)
     */
    public static final int INCLUDE = 0;

    /**
     * @see #matches(StringPattern[], String)
     */
    public static final int EXCLUDE = 1;

    private final int    mode;
    private final String pattern;

    /**
     * @param mode {@link #INCLUDE} or {@link #EXCLUDE}
     */
    public StringPattern(int mode, String pattern) {
        this.mode    = mode;
        this.pattern = pattern;
    }

    public StringPattern(String pattern) {
        this.mode    = StringPattern.INCLUDE;
        this.pattern = pattern;
    }

    public int getMode() { return this.mode; }

    /**
     * Match the given <code>text</code> against the pattern represented by the current instance,
     * as follows:
     * <ul>
     *   <li>
     *     A <code>*</code> in the pattern matches any sequence of zero or more characters in the
     *     <code>text</code>
     *   </li>
     *   <li>
     *     A <code>?</code> in the pattern matches exactly one character in the <code>text</code>
     *   </li>
     *   <li>
     *     Any other character in the pattern must appear exactly as it is in the <code>text</code>
     * </ul>
     * Notice: The <code>mode</code> flag of the current instance does not take any effect here.
     */
    public boolean matches(String text) {
        return StringPattern.wildmatch(this.pattern, text);
    }

    /**
     * Parse a "combined pattern" into an array of {@link StringPattern}s. A combined pattern
     * string is structured as follows:
     * <pre>
     *   combined-pattern :=
     *     [ '+' | '-' ] pattern
     *     { ( '+' | '-' ) pattern }
     * </pre>
     * If a pattern is preceeded with a '-', then the {@link StringPattern} is created with mode
     * {@link #EXCLUDE}, otherwise with mode {@link #INCLUDE}.
     */
    public static StringPattern[] parseCombinedPattern(String combinedPattern) {
        ArrayList al = new ArrayList();
        for (int k = 0, l; k < combinedPattern.length(); k = l) {
            int patternMode;
            char c = combinedPattern.charAt(k);
            if (c == '+') {
                patternMode = StringPattern.INCLUDE;
                ++k;
            } else
            if (c == '-') {
                patternMode = StringPattern.EXCLUDE;
                ++k;
            } else {
                patternMode = StringPattern.INCLUDE;
            }
            for (l = k; l < combinedPattern.length(); ++l) {
                c = combinedPattern.charAt(l);
                if (c == '+' || c == '-') break;
            }
            al.add(new StringPattern(patternMode, combinedPattern.substring(k, l)));
        }
        return (StringPattern[]) al.toArray(new StringPattern[al.size()]);
    }

    /**
     * Match a given <code>text</code> against an array of {@link StringPattern}s (which was
     * typically created by {@link #parseCombinedPattern(String)}.
     * <p>
     * The last matching pattern takes effect; if its mode is {@link #INCLUDE}, then
     * <code>true</code> is returned, if its mode is {@link #EXCLUDE}, then <code>false</code> is
     * returned.
     * <p>
     * If <code>patterns</code> is {@link #PATTERNS_NONE}, or empty, or none of its patterns
     * matches, then <code>false</code> is returned.
     * <p>
     * If <code>patterns</code> is {@link #PATTERNS_ALL}, then <code>true</code> is
     * returned.
     * <p>
     * For backwards compatibility, <code>null</code> patterns are treated like
     * {@link #PATTERNS_NONE}.
     */
    public static boolean matches(StringPattern[] patterns, String text) {
        if (patterns == null) return false; // Backwards compatibility -- previously, "null" was officially documented.

        for (int i = patterns.length - 1; i >= 0; --i) {
            if (patterns[i].matches(text)) {
                return patterns[i].getMode() == StringPattern.INCLUDE;
            }
        }
        return false; // No patterns defined or no pattern matches.
    }
    public static final StringPattern[] PATTERNS_ALL = new StringPattern[] { new StringPattern("*") };
    public static final StringPattern[] PATTERNS_NONE = new StringPattern[0];

    public String toString() {
        return (
            this.mode == StringPattern.INCLUDE ? '+' :
            this.mode == StringPattern.EXCLUDE ? '-' :
            '?'
        ) + this.pattern;
    }

    private static boolean wildmatch(String pattern, String text) {
        int i;
        for (i = 0; i < pattern.length(); ++i) {
            char c = pattern.charAt(i);
            switch (c) {

            case '?':
                if (i == text.length()) return false;
                break;

            case '*':
                if (pattern.length() == i + 1) return true; // Optimization for trailing '*'.
                pattern = pattern.substring(i + 1);
                for (; i <= text.length(); ++i) {
                    if (StringPattern.wildmatch(pattern, text.substring(i))) return true;
                }
                return false;

            default:
                if (i == text.length()) return false;
                if (text.charAt(i) != c) return false;
                break;
            }
        }
        return text.length() == i;
    }
}
