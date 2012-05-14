


package edu.columbia.psl.invivoexpreval;

/**
 * An {@link IClassLoader} that loads {@link IClass}es through a reflection
 * {@link ClassLoader}.
 */
public class ClassLoaderIClassLoader extends IClassLoader {
    private static final boolean DEBUG = false;

    /**
     * @param classLoader The delegate that loads the classes.
     */
    public ClassLoaderIClassLoader(ClassLoader classLoader) {
        super(
            null   // optionalParentIClassLoader
        );

        if (classLoader == null) throw new NullPointerException();

        this.classLoader = classLoader;
        super.postConstruct();
    }

    /**
     * Equivalent to
     * <pre>
     *   ClassLoaderIClassLoader(Thread.currentThread().getContextClassLoader())
     * </pre>
     */
    public ClassLoaderIClassLoader() {
        this(Thread.currentThread().getContextClassLoader());
    }

    public ClassLoader getClassLoader() {
        return this.classLoader;
    }

    protected IClass findIClass(String descriptor) throws ClassNotFoundException {

        Class clazz;
        try {

            //
            // See also [ 931385 ] Janino 2.0 throwing exception on arrays of java.io.File:
            //
            // "ClassLoader.loadClass()" and "Class.forName()" should be identical,
            // but "ClassLoader.loadClass("[Ljava.lang.Object;")" throws a
            // ClassNotFoundException under JDK 1.5.0 beta.
            // Unclear whether this a beta version bug and SUN will fix this in the final
            // release, but "Class.forName()" seems to work fine in all cases, so we
            // use that.
            //
//            clazz = this.classLoader.loadClass(Descriptor.toClassName(descriptor));
            clazz = Class.forName(Descriptor.toClassName(descriptor), false, this.classLoader);
        } catch (ClassNotFoundException e) {
            if (e.getException() == null) {
                return null;
            } else
            {
                throw e;
            }
        }
        if (ClassLoaderIClassLoader.DEBUG) System.out.println("clazz = " + clazz);

        IClass result = new ReflectionIClass(clazz, this);
        this.defineIClass(result);
        return result;
    }

    private final ClassLoader classLoader;
}
