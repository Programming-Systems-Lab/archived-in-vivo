package edu.columbia.psl.invivoexpreval.util.enumerator;

import java.util.*;

/**
 * A class that represents an enumerated value. Its main features are its {@link #toString()} and
 * {@link #fromString(String, Class)} method, which map names to values and vice versa.
 * <p>
 * To use this class, derive from it and define one or more
 * <code>public static final</code> fields, as follows:
 * <pre>
 * public final class Suit extends Enumerator {
 *
 *     // Exactly N instances of "Suit" exist to represent the N possible values.
 *     public static final Suit CLUBS    = new Suit("clubs");
 *     public static final Suit DIAMONDS = new Suit("diamonds");
 *     public static final Suit HEARTS   = new Suit("hearts");
 *     public static final Suit SPADES   = new Suit("spades");
 *
 *     // Optional, if you want to use EumeratorSet arithmetics.
 *     public static final EnumeratorSet NONE = new EnumeratorSet(Suit.class      ).setName("none");
 *     public static final EnumeratorSet ALL  = new EnumeratorSet(Suit.class, true).setName("all");
 *
 *     // These MUST be declared exactly like this:
 *     private Suit(String name) { super(name); }
 *     public static Suit fromString(String name) throws EnumeratorFormatException {
 *         return (Suit) Enumerator.fromString(name, Suit.class);
 *     }
 * }
 * </pre>
 *
 * @see <a href="http://java.sun.com/developer/Books/effectivejava/Chapter5.pdf">Effective Java, Item 21</a>
 */
public abstract class Enumerator {
    /*package*/ final String name;

    /**
     * Class enumeratorClass => Map: String name => Enumerator
     */
    private static final Map instances = Collections.synchronizedMap(new HashMap());

    /**
     * Initialize the enumerator to the given value.
     */
    protected Enumerator(String name) {
        if (name == null) throw new NullPointerException();
        this.name = name;

        Enumerator.getInstances(this.getClass()).put(name, this);
    }

    /**
     * Equality is reference identity.
     */
    public final boolean equals(Object that) {
        return this == that;
    }

    /**
     * Enforce {@link Object}'s notion of {@link Object#hashCode()}.
     */
    public final int hashCode() {
        return super.hashCode();
    }

    /**
     * Returns a mapping of name to Enumerator for the given enumeratorClass.
     */
    /*package*/ static Map getInstances(Class enumeratorClass) {
        Map m = (Map) Enumerator.instances.get(enumeratorClass);
        if (m != null) return m;

        // The map need not be synchronized because it is modified only during initialization
        // of the Enumerator.
        m = new HashMap();
        Enumerator.instances.put(enumeratorClass, m);
        return m;
    }

    /**
     * Initialize an {@link Enumerator} from a string.
     * <p>
     * The given string is converted into a value by looking at all instances of the given type
     * created so far.
     * <p>
     * Derived classes should invoke this method as follows:<pre>
     * public class Suit extends Enumerator {
     *     ...
     *     public static Suit fromString(String name) throws EnumeratorFormatException {
     *         return (Suit) Enumerator.fromString(name, Suit.class);
     *     }
     * }</pre>
     *
     * @throws EnumeratorFormatException if the string cannot be identified
     */
    protected static final Enumerator fromString(String name, Class enumeratorClass) throws Exception {
        Enumerator value = (Enumerator) Enumerator.getInstances(enumeratorClass).get(name);
        if (value == null) throw new Exception(name);
        return value;
    }

    /**
     * Returns the <code>name</code> passed to {@link #Enumerator(String)}.
     */
    public String toString() {
        return this.name;
    }
}
