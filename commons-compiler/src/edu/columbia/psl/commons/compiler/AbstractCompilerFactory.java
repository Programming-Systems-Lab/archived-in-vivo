package edu.columbia.psl.commons.compiler;

/**
 * Base class for a simple {@link ICompilerFactory}.
 */
public abstract class AbstractCompilerFactory implements ICompilerFactory {

    public abstract String getId();
    public abstract String getImplementationVersion();

    public IExpressionEvaluator newExpressionEvaluator() {
        throw new UnsupportedOperationException(this.getId() + ": newExpressionEvaluator");
    }

    public IScriptEvaluator newScriptEvaluator() {
        throw new UnsupportedOperationException(this.getId() + ": newScriptEvaluator");
    }

    public IClassBodyEvaluator newClassBodyEvaluator() {
        throw new UnsupportedOperationException(this.getId() + ": newClassBodyEvaluator");
    }

    public ISimpleCompiler newSimpleCompiler() {
        throw new UnsupportedOperationException(this.getId() + ": newSimpleCompiler");
    }

    public AbstractJavaSourceClassLoader newJavaSourceClassLoader() {
        throw new UnsupportedOperationException(this.getId() + ": newJavaSourceClassLoader");
    }

    public AbstractJavaSourceClassLoader newJavaSourceClassLoader(ClassLoader parentClassLoader) {
        throw new UnsupportedOperationException(this.getId() + ": newJavaSourceClassLoader(ClassLoader)");
    }
}
