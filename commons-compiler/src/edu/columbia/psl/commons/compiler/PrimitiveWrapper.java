


package edu.columbia.psl.commons.compiler;

/**
 * A helper class that wraps primitive values in their wrapper classes.
 */
public final class PrimitiveWrapper {
    private PrimitiveWrapper() {}

    public static Boolean   wrap(boolean v) { return v ? Boolean.TRUE : Boolean.FALSE; }
    public static Byte      wrap(byte    v) { return new Byte(v);      }
    public static Short     wrap(short   v) { return new Short(v);     }
    public static Integer   wrap(int     v) { return new Integer(v);   }
    public static Long      wrap(long    v) { return new Long(v);      }
    public static Character wrap(char    v) { return new Character(v); }
    public static Float     wrap(float   v) { return new Float(v);     }
    public static Double    wrap(double  v) { return new Double(v);    }
    public static Object    wrap(Object  v) { return v;                }
}
