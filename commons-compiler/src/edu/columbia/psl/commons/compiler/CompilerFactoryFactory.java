
package edu.columbia.psl.commons.compiler;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

/**
 * Utility class that finds implementations of {@link ICompilerFactory}s.
 */
public final class CompilerFactoryFactory {
    private CompilerFactoryFactory() {}

    private static ICompilerFactory defaultCompilerFactory = null;

    /**
     * Finds the first implementation of <code>edu.columbia.psl.commons.compiler</code> on the class path, then loads and
     * instantiates its {@link ICompilerFactory}.
     *
     * @return           The {@link ICompilerFactory} of the first implementation on the class path
     * @throws Exception Many things can go wrong while finding and initializing the default compiler factory
     */
    public static ICompilerFactory getDefaultCompilerFactory() throws Exception {
        if (defaultCompilerFactory == null) {
            Properties properties = new Properties();
            InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(
                "edu.columbia.psl.commons.compiler.properties"
            );
            if (is == null) {
                throw new ClassNotFoundException(
                    "No implementation of edu.columbia.psl.commons.compiler is on the class path"
                );
            }
            try {
                properties.load(is);
            } finally {
                is.close();
            }
            String compilerFactoryClassName = properties.getProperty("compilerFactory");
            defaultCompilerFactory = getCompilerFactory(compilerFactoryClassName);
        }
        return defaultCompilerFactory;
    }

    /**
     * Finds all implementation of <code>edu.columbia.psl.commons.compiler</code> on the class path, then loads and
     * instantiates their {@link ICompilerFactory}s.
     *
     * @return           The {@link ICompilerFactory}s of all implementations on the class path
     * @throws Exception Many things can go wrong while finding and initializing compiler factories
     */
    public static ICompilerFactory[] getAllCompilerFactories() throws Exception {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        List/*<IConpilerFactory>*/ factories = new ArrayList();
        for (Enumeration en = cl.getResources("edu.columbia.psl.commons.compiler.properties"); en.hasMoreElements();) {
            URL url = (URL) en.nextElement();

            Properties properties;
            {
                properties = new Properties();
                InputStream is = url.openStream();
                try {
                    properties.load(is);
                } finally {
                    is.close();
                }
            }

            String compilerFactoryClassName = properties.getProperty("compilerFactory");
            if (compilerFactoryClassName == null) {
                throw new IllegalStateException(url.toString() + " does not specify the 'compilerFactory' property");
            }

            factories.add(getCompilerFactory(compilerFactoryClassName));
        }
        return (ICompilerFactory[]) factories.toArray(new ICompilerFactory[factories.size()]);
    }

    /**
     * Loads an {@link ICompilerFactory} by class name.
     *
     * @param compilerFactoryClassName Name of a class that implements {@link ICompilerFactory}
     * @throws Exception               Many things can go wrong while loading and initializing the default compiler
     *                                 factory
     */
    public static ICompilerFactory getCompilerFactory(String compilerFactoryClassName) throws Exception {
        return (ICompilerFactory) Thread.currentThread().getContextClassLoader().loadClass(
            compilerFactoryClassName
        ).newInstance();
    }

    /**
     * @return The version of the commons-compiler specification, or <code>null</code>
     */
    public static String getSpecificationVersion() {
        return CompilerFactoryFactory.class.getPackage().getSpecificationVersion();
    }
}
