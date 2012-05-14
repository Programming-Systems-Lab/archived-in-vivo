


package edu.columbia.psl.invivoexpreval;

import java.util.*;

/**
 * Representation of a "method descriptor" (JVMS 4.3.3).
 */
public class MethodDescriptor {

    /** The field descriptors of the method parameters. */
    public final String[] parameterFDs;

    /** The field descriptor of the method return value. */
    public final String   returnFD;

    /** */
    public MethodDescriptor(String[] parameterFDs, String returnFD) {
        this.parameterFDs = parameterFDs;
        this.returnFD     = returnFD;
    }

    /**
     * Parse a method descriptor into parameter FDs and return FDs.
     */
    public MethodDescriptor(String s) {
        if (s.charAt(0) != '(') throw new InVivoRuntimeException();

        int from = 1;
        List parameterFDs = new ArrayList(); // String
        while (s.charAt(from) != ')') {
            int to = from;
            while (s.charAt(to) == '[') ++to;
            if ("BCDFIJSZ".indexOf(s.charAt(to)) != -1) {
                ++to;
            } else
            if (s.charAt(to) == 'L') {
                for (++to; s.charAt(to) != ';'; ++to);
                ++to;
            } else {
                throw new InVivoRuntimeException();
            }
            parameterFDs.add(s.substring(from, to));
            from = to;
        }
        this.parameterFDs = (String[]) parameterFDs.toArray(new String[parameterFDs.size()]);
        this.returnFD = s.substring(++from);
    }

    /**
     * Returns the "method descriptor" (JVMS 4.3.3).
     */
    public String toString() {
        StringBuffer sb = new StringBuffer("(");
        for (int i = 0; i < this.parameterFDs.length; ++i) sb.append(this.parameterFDs[i]);
        return sb.append(')').append(this.returnFD).toString();
    }

    /**
     * Patch an additional parameter into a given method descriptor.
     */
    public static String prependParameter(String md, String parameterFD) {
        return '(' + parameterFD + md.substring(1);
    }
}
