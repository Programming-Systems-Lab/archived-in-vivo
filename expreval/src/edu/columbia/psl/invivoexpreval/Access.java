


package edu.columbia.psl.invivoexpreval;

import edu.columbia.psl.invivoexpreval.util.enumerator.Enumerator;

/**
 * Return value for {@link IClass.IMember#getAccess}.
 * JLS2 6.6
 */
public final class Access extends Enumerator {
    public static final Access PRIVATE   = new Access("private");
    public static final Access PROTECTED = new Access("protected");
    public static final Access DEFAULT   = new Access("/*default*/");
    public static final Access PUBLIC    = new Access("public");

    // These MUST be declared exactly like this:
    private Access(String name) { super(name); }
    public static Access fromString(String name) throws Exception {
        return (Access) Enumerator.fromString(name, Access.class);
    }
}
