


package edu.columbia.psl.invivoexpreval;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.columbia.psl.commons.compiler.*;
import edu.columbia.psl.invivoexpreval.Java.*;
import edu.columbia.psl.invivoexpreval.Visitor.RvalueVisitor;
import edu.columbia.psl.invivoexpreval.util.Traverser;

/***
 * Expression evaluator for the InVivo project. Builds on code ripped out of Apache Commons JCI
 * @author nikhil
 */
public class ExprEvaluator extends ScriptEvaluator implements IExpressionEvaluator {
    private Class[] optionalExpressionTypes = null;

    public ExprEvaluator(
        String   expr,
        Class    exprType,
        String[] paramNames,
        Class[]  paramTypes
    ) throws CompileException {
        this.setExpressionType(exprType);
        this.setParameters(paramNames, paramTypes);
        this.cook(expr);
    }

    public ExprEvaluator(
        String      expression,
        Class       expressionType,
        String[]    parameterNames,
        Class[]     parameterTypes,
        Class[]     thrownExceptions,
        ClassLoader optionalParentClassLoader
    ) throws CompileException {
        this.setExpressionType(expressionType);
        this.setParameters(parameterNames, parameterTypes);
        this.setThrownExceptions(thrownExceptions);
        this.setParentClassLoader(optionalParentClassLoader);
        this.cook(expression);
    }

    public ExprEvaluator(
        String      expression,
        Class       expressionType,
        String[]    parameterNames,
        Class[]     parameterTypes,
        Class[]     thrownExceptions,
        Class       optionalExtendedType,
        Class[]     implementedTypes,
        ClassLoader optionalParentClassLoader
    ) throws CompileException {
        this.setExpressionType(expressionType);
        this.setParameters(parameterNames, parameterTypes);
        this.setThrownExceptions(thrownExceptions);
        this.setExtendedClass(optionalExtendedType);
        this.setImplementedInterfaces(implementedTypes);
        this.setParentClassLoader(optionalParentClassLoader);
        this.cook(expression);
    }

    public ExprEvaluator(
        Scanner     scanner,
        String      className,
        Class       optionalExtendedType,
        Class[]     implementedTypes,
        boolean     staticMethod,
        Class       expressionType,
        String      methodName,
        String[]    parameterNames,
        Class[]     parameterTypes,
        Class[]     thrownExceptions,
        ClassLoader optionalParentClassLoader
    ) throws CompileException, IOException {
        this.setClassName(className);
        this.setExtendedClass(optionalExtendedType);
        this.setImplementedInterfaces(implementedTypes);
        this.setStaticMethod(staticMethod);
        this.setExpressionType(expressionType);
        this.setMethodName(methodName);
        this.setParameters(parameterNames, parameterTypes);
        this.setThrownExceptions(thrownExceptions);
        this.setParentClassLoader(optionalParentClassLoader);
        this.cook(scanner);
    }

    public ExprEvaluator() {}

    public void setExpressionType(Class expressionType) {
        this.setExpressionTypes(new Class[] { expressionType });
    }

    public void setExpressionTypes(Class[] expressionTypes) {
        assertNotCooked();
        this.optionalExpressionTypes = expressionTypes;

        Class[] returnTypes = new Class[expressionTypes.length];
        for (int i = 0; i < returnTypes.length; ++i) {
            Class et = expressionTypes[i];
            returnTypes[i] = et == ANY_TYPE ? Object.class : et;
        }
        super.setReturnTypes(returnTypes);
    }

    public final void setReturnType(Class returnType) {
        throw new AssertionError("Must not be used on an ExpressionEvaluator; use 'setExpressionType()' instead");
    }

    public final void setReturnTypes(Class[] returnTypes) {
        throw new AssertionError("Must not be used on an ExpressionEvaluator; use 'setExpressionTypes()' instead");
    }

    protected Class getDefaultReturnType() {
        return Object.class;
    }

    protected List makeStatements(
        int    idx,
        Parser parser
    ) throws CompileException, IOException {
        List statements = new ArrayList();

        Rvalue value = parser.parseExpression().toRvalueOrPE();

        Class et = this.optionalExpressionTypes == null ? ANY_TYPE : this.optionalExpressionTypes[idx];
        if (et == void.class) {
            statements.add(new Java.ExpressionStatement(value));
        } else {
            if (et == ANY_TYPE) {
                value = new Java.MethodInvocation(
                    parser.location(),         // location
                    new Java.ReferenceType(     // optionalTarget
                        parser.location(),                                                           // location
                        new String[] { "org", "codehaus", "commons", "compiler", "PrimitiveWrapper" } // identifiers
                    ),
                    "wrap",                     // methodName
                    new Java.Rvalue[] { value } // arguments
                );
                PrimitiveWrapper.wrap(99);
                this.classToType(null, PrimitiveWrapper.class);
            }
            statements.add(new Java.ReturnStatement(parser.location(), value));
        }
        if (!parser.peekEOF()) {
            throw new CompileException("Unexpected token \"" + parser.peek() + "\"", parser.location());
        }

        return statements;
    }

    public static String[] guessParameterNames(Scanner scanner) throws CompileException, IOException {
        //HACK: Assumes that names follow conventions.
        Parser parser = new Parser(scanner);

        while (parser.peek("import")) parser.parseImportDeclaration();

        Rvalue rvalue = parser.parseExpression().toRvalueOrPE();
        if (!parser.peekEOF()) {
            throw new CompileException("Unexpected token \"" + parser.peek() + "\"", scanner.location());
        }

        final Set parameterNames = new HashSet();
        rvalue.accept((RvalueVisitor) new Traverser() {
            public void traverseAmbiguousName(AmbiguousName an) {

                for (int i = 0; i < an.identifiers.length; ++i) {
                    if (Character.isUpperCase(an.identifiers[i].charAt(0))) return;
                }

                parameterNames.add(an.identifiers[0]);
            }
        }.comprehensiveVisitor());

        return (String[]) parameterNames.toArray(new String[parameterNames.size()]);
    }
}
