


package edu.columbia.psl.invivoexpreval;

import java.util.*;

/**
 * This {@link ClassLoader} allows for the loading of a set of Java&trade; classes
 * provided in class file format.
 */
public class ByteArrayClassLoader extends ClassLoader {

    /**
     * The given {@link Map} of classes must not be modified afterwards.
     *
     * @param classes String className => byte[] data
     */
    public ByteArrayClassLoader(Map classes) {
        this.classes = classes;
    }

    /**
     * @see #ByteArrayClassLoader(Map)
     */
    public ByteArrayClassLoader(Map classes, ClassLoader parent) {
        super(parent);
        this.classes = classes;
    }

    /**
     * Implements {@link ClassLoader#findClass(String)}.
     * <p>
     * Notice that, although nowhere documented, no more than one thread at a time calls this
     * method, because {@link ClassLoader#loadClass(java.lang.String)} is
     * <code>synchronized</code>.
     */
    protected Class findClass(String name) throws ClassNotFoundException {
        byte[] data = (byte[]) this.classes.get(name);
        if (data == null) throw new ClassNotFoundException(name);

        // Notice: Not inheriting the protection domain will cause problems with Java Web Start /
        // JNLP. See
        //     http://jira.codehaus.org/browse/JANINO-104
        //     http://www.nabble.com/-Help-jel--java.security.AccessControlException-to13073723.html
        return super.defineClass(
            name,                                 // name
            data, 0, data.length,                 // b, off, len
            this.getClass().getProtectionDomain() // protectionDomain
        );
    }

    /**
     * An object is regarded equal to <code>this</code> iff
     * <ul>
     *   <li>It is also an instance of {@link ByteArrayClassLoader}
     *   <li>Both have the same parent {@link ClassLoader}
     *   <li>Exactly the same classes (name, bytecode) were added to both
     * </ul>
     * Roughly speaking, equal {@link ByteArrayClassLoader}s will return functionally identical
     * {@link Class}es on {@link ClassLoader#loadClass(java.lang.String)}.
     */
    public boolean equals(Object o) {
        if (!(o instanceof ByteArrayClassLoader)) return false;
        if (this == o) return true;
        ByteArrayClassLoader that = (ByteArrayClassLoader) o;

        {
            final ClassLoader parentOfThis = this.getParent();
            final ClassLoader parentOfThat = that.getParent();
            if (parentOfThis == null ? parentOfThat != null : !parentOfThis.equals(parentOfThat)) return false;
        }

        if (this.classes.size() != that.classes.size()) return false;
        for (Iterator it = this.classes.entrySet().iterator(); it.hasNext();) {
            Map.Entry me = (Map.Entry) it.next();
            byte[] ba = (byte[]) that.classes.get(me.getKey());
            if (ba == null) return false; // Key missing in "that".
            if (!Arrays.equals((byte[]) me.getValue(), ba)) return false; // Byte arrays differ.
        }
        return true;
    }
    public int hashCode() {
        int hc = this.getParent().hashCode();

        for (Iterator it = this.classes.entrySet().iterator(); it.hasNext();) {
            Map.Entry me = (Map.Entry) it.next();
            hc ^= me.getKey().hashCode();
            byte[] ba = (byte[]) me.getValue();
            for (int i = 0; i < ba.length; ++i) {
                hc = (31 * hc) ^ ba[i];
            }
        }
        return hc;
    }

    private final Map classes; // String className => byte[] data
}
