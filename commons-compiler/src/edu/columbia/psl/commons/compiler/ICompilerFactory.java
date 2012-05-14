
package edu.columbia.psl.commons.compiler;

public interface ICompilerFactory {

    String getId();

    /**
     * @return The version of <i>this</i> implementation of the commons-compiler specification, or <code>null</code>
     */
    String getImplementationVersion();

    /**
     * @throws UnsupportedOperationException The underlying implementation does not implement an {@link
     *                                       IExpressionEvaluator}
     * @see IExpressionEvaluator
     */
    IExpressionEvaluator newExpressionEvaluator();

    /**
     * @throws UnsupportedOperationException The underlying implementation does not implement an {@link
     *                                       IScriptEvaluator}
     * @see IScriptEvaluator
     */
    IScriptEvaluator newScriptEvaluator();

    /**
     * @throws UnsupportedOperationException The underlying implementation does not implement an {@link
     *                                       IClassBodyEvaluator}
     * @see IClassBodyEvaluator
     */
    IClassBodyEvaluator newClassBodyEvaluator();

    /**
     * @throws UnsupportedOperationException The underlying implementation does not implement an {@link
     *                                       ISimpleCompiler}
     * @see ISimpleCompiler
     */
    ISimpleCompiler newSimpleCompiler();

    /**
     * @throws UnsupportedOperationException The underlying implementation does not implement an {@link
     *                                       AbstractJavaSourceClassLoader}
     * @see AbstractJavaSourceClassLoader
     */
    AbstractJavaSourceClassLoader newJavaSourceClassLoader();

    /**
     * @throws UnsupportedOperationException The underlying implementation does not implement an {@link
     *                                       AbstractJavaSourceClassLoader}
     * @see AbstractJavaSourceClassLoader
     */
    AbstractJavaSourceClassLoader newJavaSourceClassLoader(ClassLoader parentClassLoader);
}
